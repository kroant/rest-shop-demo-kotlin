package cz.kromer.restshopdemo

import java.util.UUID

object TestConstants {

    const val SQL_CLEANUP = "/sql/cleanup.sql"
    const val SQL_COMPLEX_TEST_DATA = "/sql/complex-test-data.sql"

    val MILK_1_L_PRODUCT_ID: UUID = UUID.fromString("3e752234-0a19-49c0-ba18-cfebf0bb7772")
    val MILK_500_ML_PRODUCT_ID: UUID = UUID.fromString("10b10895-cce9-48c6-bc8c-7025d0a7fe57")
    val CASHEW_NUTS_PRODUCT_ID: UUID = UUID.fromString("a3c64d30-cb49-4279-9a83-282a7d0c7669")
}