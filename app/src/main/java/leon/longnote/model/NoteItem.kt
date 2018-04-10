package leon.longnote.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.databinding.ObservableField
import com.google.gson.annotations.SerializedName
import java.sql.Date

/**
 * Created by liyl9 on 2018/3/19.
 */
enum class Type {
    Y_TYPE,
    X_TYPE,
    Z_TYPE,
    END_TYPE
}

enum class ResourceType {
    AUDIO,
    IMAGE1,
    IMAGE2,
    IMAGE3,
    IMAGE4,
    SELF   //delete this note == kill self
}


@Entity(tableName = "note_table")
data class NoteItem(
        @SerializedName("type")
        @ColumnInfo(name = "type")
        var type: Int,
        @ColumnInfo(name = "textx")
        @SerializedName("textx")
        var textx: String,
        @ColumnInfo(name = "image1")
        @SerializedName("image1")
        var image1: String,
        @ColumnInfo(name = "image2")
        @SerializedName("image2")
        var image2: String,
        @ColumnInfo(name = "image3")
        @SerializedName("image3")
        var image3: String,
        @ColumnInfo(name = "image4")
        @SerializedName("image4")
        var image4: String,
        @ColumnInfo(name = "date")
        @SerializedName("date")
        var date: Date,
        @ColumnInfo(name = "voicepath")
        @SerializedName("voicepath")
        var voicepath: String,
        @ColumnInfo(name = "ismedia")
        @SerializedName("ismedia")
        var ismedia: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Int? = null// id needs to be null otherwise autoGenerate will not work and Room will use the id assigned to i

    constructor() : this(Type.X_TYPE.ordinal, "", "", "", "", "", Date(System.currentTimeMillis()), "", false)

    @Ignore
    var isChecked = ObservableField<Boolean>(false)
}
