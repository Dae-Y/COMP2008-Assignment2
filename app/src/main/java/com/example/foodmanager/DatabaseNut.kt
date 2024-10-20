package com.example.foodmanager

import android.content.Context
import androidx.room.*

@Entity(
    tableName = "nutrition",
    foreignKeys = [
        ForeignKey(
            entity = Food::class,
            parentColumns = ["id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.CASCADE // Delete nutrition when the food entry is deleted
        )
    ],
    indices = [Index("food_id")] // Create index for faster lookups
)
data class Nutrition(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "food_id") val foodId: Int, // Foreign key referencing Food
    @ColumnInfo(name = "serving_qty") val servingQty: Int,
    @ColumnInfo(name = "serving_unit") val servingUnit: String,
    @ColumnInfo(name = "serving_weight") val servingWeight: Double,
    @ColumnInfo(name = "calories") val calories: Double,
    @ColumnInfo(name = "total_fat") val totalFat: Double,
    @ColumnInfo(name = "sat_fat") val saturatedFat: Double,
    @ColumnInfo(name = "cholesterol") val cholesterol: Double,
    @ColumnInfo(name = "sodium") val sodium: Double,
    @ColumnInfo(name = "carbohydrate") val carbohydrate: Double,
    @ColumnInfo(name = "sugars") val sugars: Double,
    @ColumnInfo(name = "protein") val protein: Double,
    @ColumnInfo(name = "potassium") val potassium: Double,
    @ColumnInfo(name = "phosphorus") val phosphorus: Double
)

@Dao
interface NutritionDao {
    @Insert
    fun insertNutrition(nutrition: Nutrition)

    @Query("SELECT * FROM nutrition WHERE food_id = :foodId")
    fun getNutritionByFoodId(foodId: Int): Nutrition?

    @Delete
    fun deleteNutrition(nutrition: Nutrition)
}

@Database(entities = [Food::class, UserProfile::class, Nutrition::class], version = 2)
abstract class AppDatabase2 : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun nutritionDao(): NutritionDao
}

// Singleton pattern for accessing the database
object DatabaseProvider2 {
    private var INSTANCE: AppDatabase2? = null

    fun getDatabase(context: Context): AppDatabase2 {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase2::class.java, // Ensure class name matches
                "food-database"
            )
                .fallbackToDestructiveMigration() // Temporary fix for migration (optional)
                .allowMainThreadQueries() // Avoid in production code
                .build()

            INSTANCE = instance
            instance
        }
    }
}