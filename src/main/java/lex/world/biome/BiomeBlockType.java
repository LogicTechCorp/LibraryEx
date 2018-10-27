package lex.world.biome;

public enum BiomeBlockType
{
    FLOOR_TOP_BLOCK("floorTopBlock"),
    FLOOR_FILLER_BLOCK("floorFillerBlock"),
    WALL_BLOCK("wallBlock"),
    CEILING_FILLER_BLOCK("ceilingFillerBlock"),
    CEILING_BOTTOM_BLOCK("ceilingBottomBlock"),
    OCEAN_BLOCK("oceanBlock");

    private String identifier;

    BiomeBlockType(String identifier)
    {
        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }
}
