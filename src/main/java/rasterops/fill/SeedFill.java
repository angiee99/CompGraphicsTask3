package rasterops.fill;

import rasterization.Raster;

import java.awt.*;
import java.util.function.Predicate;

/**
 * interface for SeedFill
 */
public interface SeedFill {

     void fill(Raster img, int c, int r, Color fillColor, Predicate<Integer> isInArea);
}
