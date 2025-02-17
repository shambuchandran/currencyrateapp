package domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.mongodb.kbson.ObjectId

@Serializable
data class ApiResponse(
    val meta:MetaData,
    val data:Map<String,Currency>
)
@Serializable
data class MetaData(
    @SerialName("last_updated_at")
    val lastUpdatedAt:String
)
//@Serializable
//data class Currency(
//    val code:String,
//    val value:Double,
//    var country: String? = null,
//    var flagUrl: String? = null
//)

@Serializable
open class Currency:RealmObject{
    @PrimaryKey
    var _id:ObjectId = ObjectId()
    var code:String = ""
    var value :Double = 0.0
    var country: String? = null
    var flagUrl: String? = null

    @Transient
    var _idTransient: ObjectId? = _id

    companion object {
        const val DEFAULT = "USD"
    }
}