package com.example.mms.model.medicines

import androidx.room.Entity
import kotlinx.serialization.Serializable


@Entity
@Serializable
data class MType(
    var generic: String?,
    var complet: String?,
    var weight: String?,
) {
    constructor() : this(null, null, null)
}


@Entity
@Serializable
data class Usage(
    var route_administration: String?,
    var condition_prescription_delivery: String?,
    var link_help: String?,
) {
    constructor() : this(null, null, null)
}


@Entity
@Serializable
data class Composition(
    var substance_code: Int?,
    var substance_name: String?,
    var substance_dosage: String?,
    var substance_reference: String?,
    var composant_type: String?,
    var sa_ft_num: String?,
) {
    constructor() : this(null, null, null, null, null, null)
}


@Entity
@Serializable
data class SecurityInformations(
    var start_date: String?,
    var end_date: String?,
    var text: String?,
) {
    constructor() : this(null, null, null)
}


@Entity
@Serializable
data class Availbility(
    var code_statut: Int?,
    var statut: String?,
    var start_date: String?,
    var update_date: String?,
    var end_date: String?,
    var informations_link: String?,
) {
    constructor() : this(null, null, null, null, null, null)
}


@Entity
@Serializable
data class SalesInfos(
    var administrative_status: String?,
    var holder: String?,
    var surveillance: Boolean?,
    var CIP7: Long?,
    var CIP13: Long?,
    var presentation: String?,
    var presentation_status: String?,
    var is_on_sale: Boolean?,
    var date_marketing_declaration: String?,
    var refund_rate: Int?,
    var refund_conditions: String?,
    var price_no_tax: Float?,
    var full_price: Float?,
) {
    // ??????????
    fun setIs_on_sale(is_on_sale: Boolean) {
        this.is_on_sale = is_on_sale
    }

    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}


@Entity
@Serializable
data class GenericGroup(
    var generic_group_id: Int?,
    var generic_group_name: String?,
    var generic_type: Int?,
    var num: Int?,
) {
    constructor() : this(null, null, null, null)
}
