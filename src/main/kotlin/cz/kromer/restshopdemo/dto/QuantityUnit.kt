package cz.kromer.restshopdemo.dto

enum class QuantityUnit(
    val maxScale: Int
) {
    PIECE(0),
    GRAM(0),
    LITER(3),
    METER(3);
}