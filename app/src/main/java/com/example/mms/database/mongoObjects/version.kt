package com.example.mms.database.mongoObjects

import com.example.mms.model.Version
import kotlinx.serialization.Serializable

@Serializable
class MongoVersion (
    var version: Int,
    var updated_documents_cis: List<Int>,
) {
    fun toVersion(): Version {
        return Version(0, this.version)
    }

    override fun toString(): String {
        return "MongoVersion(version=$version, updated_documents_cis=${updated_documents_cis.size})"
    }
}
