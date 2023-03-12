package tk.airshipcraft.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * Checks for if a tree should be custom as well as growable and "natural" materials.
 */
@Getter
@Setter
public class TreeUtils {

    private int weight;
    private int xOffset;
    private int yOffset;
    private int zOffset;
    private String name;
    private Material material;

    /**
     * Checks if the material is to be considered "natural"
     * i.e. using unnatural blocks like fences/campfires as branches, etc.
     * @param material
     * @return boolean
     */
    public static boolean isNaturalMaterial(Material material) {
        if (material.isSolid() &&
                material != Material.OAK_LEAVES &&
                material != Material.BIRCH_LEAVES &&
                material != Material.SPRUCE_LEAVES &&
                material != Material.JUNGLE_LEAVES &&
                material != Material.DARK_OAK_LEAVES &&
                material != Material.ACACIA_LEAVES &&
                material != Material.GRASS &&
                material != Material.GRASS_BLOCK &&
                material != Material.TALL_GRASS &&
                material != Material.DIRT &&
                material != Material.MYCELIUM &&
                material != Material.PODZOL &&
                material != Material.WARPED_HYPHAE &&
                material != Material.WARPED_NYLIUM &&
                material != Material.CRIMSON_HYPHAE &&
                material != Material.CRIMSON_NYLIUM &&
                material != Material.CRIMSON_FUNGUS &&
                material != Material.WARPED_FUNGUS &&
                material != Material.WARPED_WART_BLOCK &&
                material != Material.NETHER_WART_BLOCK &&
                material != Material.SHROOMLIGHT &&
                material != Material.TWISTING_VINES &&
                material != Material.WEEPING_VINES &&
                material != Material.CAMPFIRE &&
                material != Material.SOUL_CAMPFIRE &&
                material != Material.STONE &&
                material != Material.OAK_LOG &&
                material != Material.CRIMSON_ROOTS &&
                material != Material.WARPED_ROOTS &&
                material != Material.BIRCH_LOG &&
                material != Material.SPRUCE_LOG &&
                material != Material.JUNGLE_LOG &&
                material != Material.DARK_OAK_LOG &&
                material != Material.OAK_SAPLING &&
                material != Material.BIRCH_SAPLING &&
                material != Material.JUNGLE_SAPLING &&
                material != Material.ACACIA_SAPLING &&
                material != Material.DARK_OAK_SAPLING &&
                material != Material.SPRUCE_SAPLING &&
                material != Material.ACACIA_LOG) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the block is growable,
     * i.e. is the block a sapling, mushroom, fungus etc
     * @param material
     * @return boolean
     */
    public static boolean isGrowableMaterial(Material material) {
        if (material != Material.OAK_SAPLING &&
                material != Material.BIRCH_SAPLING &&
                material != Material.SPRUCE_SAPLING &&
                material != Material.JUNGLE_SAPLING &&
                material != Material.DARK_OAK_SAPLING &&
                material != Material.ACACIA_SAPLING &&
                material != Material.WARPED_FUNGUS &&
                material != Material.CRIMSON_FUNGUS &&
                material != Material.BROWN_MUSHROOM &&
                material != Material.RED_MUSHROOM) {
            return false;
        }
        return true;
    }
}
