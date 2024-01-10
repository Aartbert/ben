package nl.han.shared.datastructures.world;

import nl.han.shared.enums.StructureType;

/**
 * This record provides a blueprint for Structure objects.
 *
 * @author Jordan Geurtsen
 * @see StructureType
 */
public record Structure(Coordinate location, StructureType structureType) {

}