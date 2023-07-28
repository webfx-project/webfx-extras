package dev.webfx.extras.theme;

public interface Theme {

    boolean supportsFacetCategory(Object facetCategory);

    void styleFacet(Facet facet, Object facetCategory);

}
