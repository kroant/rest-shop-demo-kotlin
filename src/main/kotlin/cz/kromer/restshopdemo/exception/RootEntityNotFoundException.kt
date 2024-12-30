package cz.kromer.restshopdemo.exception

import java.util.UUID

class RootEntityNotFoundException(
    val id: UUID
) : RuntimeException("Entity not found: $id")