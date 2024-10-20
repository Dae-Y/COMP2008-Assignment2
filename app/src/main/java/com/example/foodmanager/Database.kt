package com.example.foodmanager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

// Define the Food entity for the Room database.
@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "food_name") val name: String,
    @ColumnInfo(name = "portion_size") val portion: Int,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo(name = "kcal") val kcal: Double,
    @ColumnInfo(name = "date") val date: String, // Use "yyyy-MM-dd" format for consistency
    @ColumnInfo(name = "food_image") val image: String? // Store the path of the image

)

// When Remote API calling fails, users should see this manually typed nutrition
// Nutrition entity
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

// Define dailyKcal entity, this table only uses 1 row.
// So it only use update.
@Entity(tableName = "userProfile")
data class UserProfile(
    @PrimaryKey var deviceID: Int, // unique id for devices for firebase picture storing
    @ColumnInfo(name = "daily_Limit") var kcalDailyLimit: Double// daily kcal limit
)

// Data Access Object, DAO is an interface that defines methods for accessing the database.
@Dao
interface FoodDao {

    // ======================== When API fails, open up nutrition manually ===================================== //
    // @Insert
    // fun insertNutrition(nutrition: Nutrition)

    // ======================================= Query For dailyKcal Table ======================================= //
    @Update
    fun updateUserProfile(userProfile: UserProfile)

    @Insert
    fun newUserProfile(userProfile: UserProfile) // IMPORTANT: TO BE CALLED ONLY ONCE PER DEVICE

    @Query("SELECT EXISTS(SELECT 1 FROM userProfile)")
    fun hasProfile(): Boolean

    @Query("SELECT * FROM userProfile")
    fun getUserProfile(): UserProfile

    // ========================================= Query For Food table ========================================= //
    @Query("SELECT * FROM foods") // Get all foods
    fun getAllFoods(): List<Food>

    @Query("SELECT * FROM foods WHERE date = :date") // Get foods by date
    fun getFoodsByDate(date: String): List<Food>

    @Query("SELECT * FROM foods WHERE id = :id") // Get food by ID
    fun getFoodById(id: Int): Food?

    @Query("SELECT * FROM foods WHERE date BETWEEN :startDate AND :endDate") // Get foods by date range
    fun getFoodsByDateRange(startDate: String, endDate: String): List<Food>

    @Query("SELECT SUM(kcal) FROM foods WHERE date = :currDate") // Getting total Kcal in a (Specific)day
    fun getDayTotalKcal(currDate: String): Int

    @Query("SELECT COUNT(*) FROM foods") // Count total foods
    fun countTotalFoods(): Int

    @Query("DELETE FROM foods WHERE date = :date") // Delete foods by date
    fun deleteFoodsByDate(date: String)

    @Query("SELECT * FROM foods ORDER BY date DESC") // Get foods ordered by date
    fun getFoodsOrderedByDate(): List<Food>

    @Query("SELECT id FROM foods ORDER BY id DESC LIMIT 1") // Getting latest food ID
    fun getLatestFoodId(): Int

    @Insert
    fun insertFood(food: Food)

    @Update
    fun updateFood(food: Food)

    @Delete
    fun deleteFood(food: Food)
}

// DAO for Nutrition
@Dao
interface NutritionDao {
    @Insert
    fun insertNutrition(nutrition: Nutrition)

    @Query("SELECT * FROM nutrition WHERE food_id = :foodId")
    fun getNutritionByFoodId(foodId: Int): Nutrition?

    @Delete
    fun deleteNutrition(nutrition: Nutrition)
}


// Database class must be an abstract class that extends RoomDatabase
// For each DAO class that is associated with the database, the database class
// must define an abstract method that has zero arguments and returns an
// instance of the DAO class
@Database(entities = [Food::class, UserProfile::class, Nutrition::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun nutritionDao(): NutritionDao
}


// Singleton pattern for accessing the database
object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "food-database"
            )
                .fallbackToDestructiveMigration() // Optional, use for migration handling
                .allowMainThreadQueries() // Avoid in production code
                .build()

            INSTANCE = instance
            instance
        }
    }
}