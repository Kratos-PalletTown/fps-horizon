package pueblopaleta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KratosProfiles
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "fps_horizon_profiles.json";
    private static List<Profile> profiles = new ArrayList<>();

    public enum ProfileType { EXACT, RANGE }

    public static class Profile {
        public String name;
        public ProfileType type;
        public int rdExact;    // usado si type == EXACT
        public int rdMin;      // usado si type == RANGE
        public int rdMax;      // usado si type == RANGE
        public int vertical;   // slider 2-40
        public int horizontal; // slider 0-100

        public Profile() {}

        public Profile(String name, ProfileType type, int rdExact, int rdMin, int rdMax,
                       int vertical, int horizontal) {
            this.name       = name;
            this.type       = type;
            this.rdExact    = rdExact;
            this.rdMin      = rdMin;
            this.rdMax      = rdMax;
            this.vertical   = vertical;
            this.horizontal = horizontal;
        }

        public boolean matches(int rd) {
            if (type == ProfileType.EXACT) return rd == rdExact;
            return rd >= rdMin && rd <= rdMax;
        }

        public String displayName() {
            if (type == ProfileType.EXACT)
                return name + " [RD=" + rdExact + "]";
            return name + " [RD " + rdMin + "-" + rdMax + "]";
        }
    }

    public static List<Profile> getProfiles() { return profiles; }

    /** Busca el perfil que aplica al RD dado. Exacto tiene prioridad sobre rango. */
    public static Profile findForRD(int rd) {
        Profile rangeMatch = null;
        for (final Profile p : profiles) {
            if (!p.matches(rd)) continue;
            if (p.type == ProfileType.EXACT) return p;
            if (rangeMatch == null) rangeMatch = p;
        }
        return rangeMatch;
    }

    /**
     * Verifica si ya existe un perfil que cubra ese RD.
     * @return el perfil conflictivo, o null si no hay conflicto.
     */
    public static Profile findConflict(ProfileType type, int rdExact, int rdMin, int rdMax,
                                        Profile exclude) {
        for (final Profile p : profiles) {
            if (p == exclude) continue;
            boolean conflicts;
            if (type == ProfileType.EXACT) {
                conflicts = p.matches(rdExact);
            } else {
                // Rango conflicta si se solapa con otro perfil
                conflicts = p.matches(rdMin) || p.matches(rdMax)
                        || (type == ProfileType.RANGE && rdMin <= getRDMax(p) && rdMax >= getRDMin(p));
            }
            if (conflicts) return p;
        }
        return null;
    }

    private static int getRDMin(Profile p) {
        return p.type == ProfileType.EXACT ? p.rdExact : p.rdMin;
    }
    private static int getRDMax(Profile p) {
        return p.type == ProfileType.EXACT ? p.rdExact : p.rdMax;
    }

    public static void addProfile(Profile p)    { profiles.add(p); save(); }
    public static void removeProfile(Profile p) { profiles.remove(p); save(); }
    public static void removeProfiles(List<Profile> toRemove) { profiles.removeAll(toRemove); save(); }
    public static void updateProfile(Profile p) { save(); }

    public static void load() {
        final Path path = getPath();
        if (!path.toFile().exists()) { profiles = new ArrayList<>(); return; }
        try (Reader r = new FileReader(path.toFile())) {
            final Type listType = new TypeToken<List<Profile>>(){}.getType();
            final List<Profile> loaded = GSON.fromJson(r, listType);
            profiles = loaded != null ? loaded : new ArrayList<>();
        } catch (Exception e) {
            profiles = new ArrayList<>();
        }
    }

    public static void save() {
        try (Writer w = new FileWriter(getPath().toFile())) {
            GSON.toJson(profiles, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Path getPath() {
        return FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
    }
}
