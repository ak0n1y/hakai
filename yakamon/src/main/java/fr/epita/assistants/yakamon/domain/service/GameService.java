package fr.epita.assistants.yakamon.domain.service;

import fr.epita.assistants.yakamon.data.model.GameModel;
import fr.epita.assistants.yakamon.data.model.ItemModel;
import fr.epita.assistants.yakamon.data.model.PlayerModel;
import fr.epita.assistants.yakamon.data.model.YakadexEntryModel;
import fr.epita.assistants.yakamon.data.model.YakamonModel;
import fr.epita.assistants.yakamon.data.repository.GameRepository;
import fr.epita.assistants.yakamon.data.repository.ItemRepository;
import fr.epita.assistants.yakamon.data.repository.PlayerRepository;
import fr.epita.assistants.yakamon.data.repository.YakadexEntryRepository;
import fr.epita.assistants.yakamon.data.repository.YakamonRepository;
import fr.epita.assistants.yakamon.domain.entity.CatchEntity;
import fr.epita.assistants.yakamon.domain.entity.CollectEntity;
import fr.epita.assistants.yakamon.domain.entity.FeedEntity;
import fr.epita.assistants.yakamon.domain.entity.GameEntity;
import fr.epita.assistants.yakamon.domain.entity.MoveEntity;
import fr.epita.assistants.yakamon.utils.Direction;
import fr.epita.assistants.yakamon.utils.tile.Collectible;
import fr.epita.assistants.yakamon.utils.tile.CollectibleType;
import fr.epita.assistants.yakamon.utils.tile.CollectibleUtils;
import fr.epita.assistants.yakamon.utils.tile.ItemType;
import fr.epita.assistants.yakamon.utils.tile.TerrainType;
import fr.epita.assistants.yakamon.utils.tile.TileType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class GameService {

    @Inject GameRepository gameRepository;
    @Inject PlayerRepository playerRepository;
    @Inject ItemRepository itemRepository;
    @Inject YakamonRepository yakamonRepository;
    @Inject YakadexEntryRepository yakadexEntryRepository;

    @ConfigProperty(name = "JWS_TICK_DURATION", defaultValue = "100")
    Integer tickDurationMs;

    @ConfigProperty(name = "JWS_MOVEMENT_DELAY", defaultValue = "2")
    Integer movementDelayTicks;

    @ConfigProperty(name = "JWS_COLLECT_DELAY", defaultValue = "5")
    Integer collectDelayTicks;

    @ConfigProperty(name = "JWS_COLLECT_MULTIPLIER", defaultValue = "4")
    Integer collectMultiplier;

    private List<List<TileType>> currentMap;
    private int mapHeight;
    private int mapWidth;
    private Map<TerrainType, Character> terrainToChar;
    private Map<String, Character> collectibleKeyToChar;

    private static String keyOf(Collectible c) {
        if (c == null) return "null|null";
        return c.getCollectibleType() + "|" + c.getValue();
    }

    private void ensureReverseMaps() {
        if (terrainToChar != null && collectibleKeyToChar != null) return;

        terrainToChar = new EnumMap<>(TerrainType.class);
        collectibleKeyToChar = new HashMap<>();

        for (char ch = 33; ch <= 126; ch++) {
            TerrainType t = TerrainType.getTerrain(ch);
            if (t != null && !terrainToChar.containsKey(t)) {
                terrainToChar.put(t, ch);
            }
        }

        for (char ch = 33; ch <= 126; ch++) {
            Collectible c = CollectibleUtils.getCollectible(ch);
            if (c != null) {
                collectibleKeyToChar.putIfAbsent(keyOf(c), ch);
            }
        }

        collectibleKeyToChar.putIfAbsent("ITEM|NONE", 'N');
    }

    private String encodeRowPlain(List<TileType> row) {
        ensureReverseMaps();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < row.size(); i++) {
            TileType tile = row.get(i);
            TerrainType t = (tile == null) ? null : tile.getTerrainType();
            Collectible c = (tile == null) ? null : tile.getCollectible();

            Character tc = (t == null) ? null : terrainToChar.get(t);
            Character cc = collectibleKeyToChar.get(keyOf(c));

            if (tc == null || cc == null) {
                throw new IllegalStateException("Cannot encode map (missing char mapping).");
            }

            if (i > 0) sb.append(' ');
            sb.append(tc).append(cc);
        }

        return sb.toString();
    }

    private String encodeCurrentMapToString() {
        if (currentMap == null || currentMap.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < currentMap.size(); y++) {
            if (y > 0) sb.append(';');
            sb.append(encodeRowPlain(currentMap.get(y)));
        }
        return sb.toString();
    }

    private void persistCurrentMap() {
        GameModel game = gameRepository.listAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Game not started."));
        game.setMap(encodeCurrentMapToString());
        gameRepository.persist(game);
    }

    @Transactional
    public GameEntity startNewGame(String mapPath, String playerName) {
        yakamonRepository.deleteAll();
        itemRepository.deleteAll();
        playerRepository.deleteAll();
        gameRepository.deleteAll();

        for (YakadexEntryModel e : yakadexEntryRepository.listAll()) {
            trySetCaughtFalse(e);
            yakadexEntryRepository.persist(e);
        }

        List<String> lines = readAllLinesOrThrow(mapPath);
        List<String> normalized = normalizeLines(lines);

        GameModel game = new GameModel();
        game.setMap(String.join(";", normalized));
        gameRepository.persist(game);

        List<List<TileType>> grid = decodeMap(normalized);
        this.currentMap = grid;
        this.mapHeight = grid.size();
        this.mapWidth = computeMaxWidth(grid);

        PlayerModel player = new PlayerModel();
        player.setUuid(UUID.randomUUID());
        String safeName = (playerName == null ? "" : playerName.trim());
        player.setName(safeName);
        player.setPosX(0);
        player.setPosY(0);
        player.setLastMove(null);
        player.setLastCollect(null);
        player.setLastCatch(null);
        player.setLastFeed(null);
        playerRepository.persist(player);

        ItemModel yakaballs = new ItemModel();
        yakaballs.setType(ItemType.YAKABALL);
        yakaballs.setQuantity(5);
        itemRepository.persist(yakaballs);

        return new GameEntity(player.getUuid(), grid);
    }

    @Transactional
    public MoveEntity move(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Invalid `direction` provided.");
        }
        if (currentMap == null || currentMap.isEmpty()) {
            throw new IllegalStateException("Game not started.");
        }

        PlayerModel player = getSinglePlayerOrThrow();
        ensureMoveDelay(player);

        int x = player.getPosX();
        int y = player.getPosY();

        int nx = x;
        int ny = y;

        switch (direction) {
            case UP -> ny = y - 1;
            case DOWN -> ny = y + 1;
            case LEFT -> nx = x - 1;
            case RIGHT -> nx = x + 1;
        }

        if (!inBounds(nx, ny)) {
            throw new IllegalArgumentException("Invalid `direction` provided.");
        }

        TileType target = currentMap.get(ny).get(nx);
        if (target == null || target.getTerrainType() == null || !isWalkable(target.getTerrainType())) {
            throw new IllegalArgumentException("Invalid `direction` provided.");
        }

        player.setPosX(nx);
        player.setPosY(ny);
        player.setLastMove(LocalDateTime.now());
        playerRepository.persist(player);

        return new MoveEntity(player.getUuid(), nx, ny, target);
    }

    private void ensureMoveDelay(PlayerModel player) {
        if (player == null) throw new IllegalStateException("Game not started.");
        if (player.getLastMove() == null) return;

        int tick = (tickDurationMs == null || tickDurationMs <= 0) ? 100 : tickDurationMs;
        int delayTicks = (movementDelayTicks == null || movementDelayTicks < 0) ? 0 : movementDelayTicks;
        long requiredMs = (long) tick * (long) delayTicks;

        long elapsedMs = Duration.between(player.getLastMove(), LocalDateTime.now()).toMillis();
        if (elapsedMs < requiredMs) {
            throw new IllegalStateException("You moved too recently.");
        }
    }

    @Transactional
    public CollectEntity collect() {
        if (currentMap == null || currentMap.isEmpty()) {
            throw new IllegalStateException("Game not started.");
        }

        PlayerModel player = getSinglePlayerOrThrow();
        ensureCollectDelay(player);

        int x = player.getPosX();
        int y = player.getPosY();

        TileType tile = currentMap.get(y).get(x);
        Collectible collectible = (tile == null) ? null : tile.getCollectible();

        if (collectible == null || collectible.getCollectibleType() != CollectibleType.ITEM) {
            throw new IllegalArgumentException("Invalid tile.");
        }

        String v = collectible.getValue();
        if (v == null || v.equalsIgnoreCase("NONE")) {
            throw new IllegalArgumentException("Invalid tile.");
        }

        ItemType itemType;
        try {
            itemType = ItemType.valueOf(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid tile.");
        }

        int mult = (collectMultiplier == null || collectMultiplier <= 0) ? 1 : collectMultiplier;

        ItemModel item = itemRepository.find("type", itemType).firstResult();
        if (item == null) {
            item = new ItemModel();
            item.setType(itemType);
            item.setQuantity(mult);
            itemRepository.persist(item);
        } else {
            Integer q = item.getQuantity();
            item.setQuantity((q == null ? 0 : q) + mult);
            itemRepository.persist(item);
        }

        Collectible empty = CollectibleUtils.getCollectible('N');
        TileType updatedTile = new TileType(tile.getTerrainType(), empty);
        currentMap.get(y).set(x, updatedTile);

        persistCurrentMap();

        player.setLastCollect(LocalDateTime.now());
        playerRepository.persist(player);

        return new CollectEntity(updatedTile);
    }

    private void ensureCollectDelay(PlayerModel player) {
        if (player == null) throw new IllegalStateException("Game not started.");
        if (player.getLastCollect() == null) return;

        int tick = (tickDurationMs == null || tickDurationMs <= 0) ? 100 : tickDurationMs;
        int delayTicks = (collectDelayTicks == null || collectDelayTicks < 0) ? 0 : collectDelayTicks;
        long requiredMs = (long) tick * (long) delayTicks;

        long elapsedMs = Duration.between(player.getLastCollect(), LocalDateTime.now()).toMillis();
        if (elapsedMs < requiredMs) {
            throw new IllegalStateException("You collected too recently.");
        }
    }

    @Transactional
    public CatchEntity catchYakamon() {
        if (currentMap == null || currentMap.isEmpty()) {
            throw new IllegalStateException("Game not started.");
        }

        PlayerModel player = getSinglePlayerOrThrow();
        int x = player.getPosX();
        int y = player.getPosY();

        TileType tile = currentMap.get(y).get(x);
        Collectible collectible = tile.getCollectible();

        if (collectible == null || collectible.getCollectibleType() != CollectibleType.YAKAMON) {
            throw new IllegalStateException("Nothing to catch.");
        }

        ItemModel yakaballs = itemRepository.find("type", ItemType.YAKABALL).firstResult();
        if (yakaballs == null || yakaballs.getQuantity() == null || yakaballs.getQuantity() <= 0) {
            throw new IllegalStateException("No yakaball.");
        }

        yakaballs.setQuantity(yakaballs.getQuantity() - 1);
        itemRepository.persist(yakaballs);

        String yakamonName = collectible.getValue();
        YakadexEntryModel entry = findEntryByNameIgnoreCase(yakamonName);
        if (entry == null || entry.id == null) {
            throw new IllegalStateException("Unknown yakamon.");
        }

        YakamonModel yk = new YakamonModel();
        yk.setNickname(null);
        yk.setEnergyPoints(100);
        yk.setYakadexEntryId(entry.id);
        yakamonRepository.persist(yk);

        entry.caught = true;
        yakadexEntryRepository.persist(entry);

        Collectible empty = CollectibleUtils.getCollectible('N');
        currentMap.get(y).set(x, new TileType(tile.getTerrainType(), empty));

        persistCurrentMap();

        player.setLastCatch(LocalDateTime.now());
        playerRepository.persist(player);

        return new CatchEntity(player.getUuid(), x, y, collectible, yakaballs.getQuantity());
    }

    @Transactional
    public FeedEntity feed(UUID yakamonUuid) {
        if (yakamonUuid == null) {
            throw new IllegalArgumentException("Invalid `yakamonUuid` provided.");
        }
        if (currentMap == null || currentMap.isEmpty()) {
            throw new IllegalStateException("Game not started.");
        }

        PlayerModel player = getSinglePlayerOrThrow();

        YakamonModel yk = yakamonRepository.find("uuid", yakamonUuid).firstResult();
        if (yk == null) {
            throw new IllegalStateException("Unknown yakamon.");
        }

        ItemModel scrooge = itemRepository.find("type", ItemType.SCROOGE).firstResult();
        if (scrooge == null || scrooge.getQuantity() == null || scrooge.getQuantity() <= 0) {
            throw new IllegalStateException("No scrooge.");
        }

        int remaining = scrooge.getQuantity() - 1;
        scrooge.setQuantity(remaining);
        itemRepository.persist(scrooge);

        int currentEnergy = (yk.getEnergyPoints() == null ? 0 : yk.getEnergyPoints());
        int newEnergy = currentEnergy + 1;
        yk.setEnergyPoints(newEnergy);
        yakamonRepository.persist(yk);

        player.setLastFeed(LocalDateTime.now());
        playerRepository.persist(player);

        return new FeedEntity(player.getUuid(), yakamonUuid, newEnergy, remaining);
    }

    private PlayerModel getSinglePlayerOrThrow() {
        return playerRepository.listAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No player found."));
    }

    private boolean inBounds(int x, int y) {
        if (y < 0 || y >= mapHeight || x < 0) return false;
        List<TileType> row = currentMap.get(y);
        return row != null && x < row.size();
    }

    private boolean isWalkable(TerrainType t) {
        return t != null && t.isWalkable();
    }

    private YakadexEntryModel findEntryByNameIgnoreCase(String name) {
        if (name == null) return null;
        for (YakadexEntryModel e : yakadexEntryRepository.listAll()) {
            if (e != null && e.name != null && e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    private static int computeMaxWidth(List<List<TileType>> grid) {
        int max = 0;
        if (grid == null) return 0;
        for (List<TileType> row : grid) {
            if (row != null && row.size() > max) max = row.size();
        }
        return max;
    }

    private static List<String> readAllLinesOrThrow(String mapPath) {
        if (mapPath == null || mapPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid mapPath: " + mapPath);
        }
        String mp = mapPath.trim();

        Path p = Path.of(mp);
        if (Files.exists(p) && Files.isRegularFile(p)) {
            try {
                return Files.readAllLines(p);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid mapPath: " + mapPath, e);
            }
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        List<String> lines = tryReadClasspathLines(cl, "maps/" + mp);
        if (lines != null) return lines;

        lines = tryReadClasspathLines(cl, mp);
        if (lines != null) return lines;

        throw new IllegalArgumentException("Invalid mapPath: " + mapPath);
    }

    private static List<String> tryReadClasspathLines(ClassLoader cl, String resourceName) {
        try (InputStream is = cl.getResourceAsStream(resourceName)) {
            if (is == null) return null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            List<String> out = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                out.add(line);
            }
            return out;
        } catch (IOException e) {
            return null;
        }
    }

    private static void trySetCaughtFalse(YakadexEntryModel e) {
        try {
            var f = e.getClass().getField("caught");
            f.set(e, Boolean.FALSE);
            return;
        } catch (Exception ignored) {
        }
        try {
            var m = e.getClass().getMethod("setCaught", Boolean.class);
            m.invoke(e, Boolean.FALSE);
        } catch (Exception ignored) {
        }
    }

    private static int firstCommentIndex(String s) {
        if (s == null) return -1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '#') {
                if (i == 0 || Character.isWhitespace(s.charAt(i - 1))) {
                    return i;
                }
            }

            if (c == '/' && i + 1 < s.length() && s.charAt(i + 1) == '/') {
                if (i == 0 || Character.isWhitespace(s.charAt(i - 1))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static boolean isSkippable(char c) {
        return Character.isWhitespace(c) || c == ',' || c == ';' || c == '|' || c == '\t';
    }

    private static void skipSkippables(String raw, int[] idx) {
        int i = idx[0];
        while (i < raw.length() && isSkippable(raw.charAt(i))) i++;
        idx[0] = i;
    }

    private static List<String> normalizeLines(List<String> lines) {
        if (lines == null) return List.of();
        List<String> normalized = new ArrayList<>();
        for (String raw0 : lines) {
            if (raw0 == null) continue;
            String raw = raw0.replace("\r", "");
            int cut = firstCommentIndex(raw);
            if (cut >= 0) raw = raw.substring(0, cut);
            if (raw.trim().isEmpty()) continue;
            normalized.add(raw.trim());
        }
        return normalized;
    }

    private static List<List<TileType>> decodeMap(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Invalid map format.");
        }

        List<List<TileType>> out = new ArrayList<>();
        Integer expectedWidth = null;

        for (String raw0 : lines) {
            if (raw0 == null) continue;

            String raw = raw0.replace("\r", "");
            int cut = firstCommentIndex(raw);
            if (cut >= 0) raw = raw.substring(0, cut);

            if (raw.trim().isEmpty()) continue;

            List<TileType> row = new ArrayList<>();
            int[] idx = new int[]{0};

            while (true) {
                skipSkippables(raw, idx);
                int i = idx[0];
                if (i >= raw.length()) break;

                int count = 0;
                boolean hasDigits = false;
                while (i < raw.length() && Character.isDigit(raw.charAt(i))) {
                    hasDigits = true;
                    count = count * 10 + (raw.charAt(i) - '0');
                    i++;
                }
                if (!hasDigits) count = 1;

                idx[0] = i;
                skipSkippables(raw, idx);
                i = idx[0];

                if (i >= raw.length()) throw new IllegalArgumentException("Invalid map format.");
                char terrainChar = raw.charAt(i);
                i++;

                idx[0] = i;
                skipSkippables(raw, idx);
                i = idx[0];

                if (i >= raw.length()) throw new IllegalArgumentException("Invalid map format.");
                char collectibleChar = raw.charAt(i);
                i++;

                idx[0] = i;

                TerrainType terrain = TerrainType.getTerrain(terrainChar);
                Collectible collectible = CollectibleUtils.getCollectible(collectibleChar);
                if (terrain == null || collectible == null) {
                    throw new IllegalArgumentException("Invalid map format.");
                }

                for (int k = 0; k < count; k++) {
                    row.add(new TileType(terrain, collectible));
                }
            }

            if (row.isEmpty()) {
                throw new IllegalArgumentException("Invalid map format.");
            }

            if (expectedWidth == null) {
                expectedWidth = row.size();
            } else if (row.size() != expectedWidth) {
                throw new IllegalArgumentException("Invalid map format.");
            }

            out.add(row);
        }

        if (out.isEmpty()) {
            throw new IllegalArgumentException("Invalid map format.");
        }

        return out;
    }
}
