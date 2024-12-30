package cz.kromer.restshopdemo.exception

import java.util.UUID

class AssociatedEntityNotFoundException(
    val id: UUID
) : RuntimeException("Entity not found: $id")