package anissia.domain

//import anissia.misc.As
//import anissia.misc.As.Companion.toClassByJson
//import com.fasterxml.jackson.core.type.TypeReference
import java.time.LocalDateTime
import javax.persistence.*

/**
 * asl is Anissia Log
 */
@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(columnNames = ["aslNo"])],
        indexes = [
            Index(columnList = "pub,active,actDt"),
            Index(columnList = "active,actDt")
        ]
)
data class Asl (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        var aslNo: Long = 0,

        @Column(nullable = false)
        var pub: Boolean = false,

        @Column(nullable = false)
        var active: Boolean = false,

        @Column(nullable = false, length = 100)
        var cmd: String = "",

        @Column(nullable = false)
        var un: Long = 0,

        @Column(nullable = false, length = 32)
        var status: String = "",

        @Column(nullable = true)
        var data1: String? = null,

        @Column(nullable = true)
        var data2: String? = null,

        @Column(nullable = true)
        var data3: String? = null,

        @Column(nullable = false)
        var actDt: LocalDateTime = LocalDateTime.now()
) {
//    fun setData1(obj: Any?) {
//        data1 = obj?.run { As.toJson(this) }
//    }
//
//    fun setData2(obj: Any?) {
//        data2 = obj?.run { As.toJson(this) }
//    }
//
//    fun setData3(obj: Any?) {
//        data3 = obj?.run { As.toJson(this) }
//    }
//
//    fun <T> readData1(typeReference: TypeReference<T>): T?
//            = data1?.run { this.toClassByJson(typeReference) }
//
//    fun <T> readData2(typeReference: TypeReference<T>): T?
//            = data2?.run { this.toClassByJson(typeReference) }
//
//    fun <T> readData3(typeReference: TypeReference<T>): T?
//            = data3?.run { this.toClassByJson(typeReference) }
}