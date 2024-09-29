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
    @ColumnInfo(name = "kcal") val kcal: Int,
    @ColumnInfo(name = "date") val date: String // Use "yyyy-MM-dd" format for consistency
)

// Data Access Object, DAO is an interface that defines methods for accessing the database.
@Dao
interface FoodDao {
    @Query("SELECT * FROM foods") // Get all foods
    fun getAllFoods(): List<Food>

    @Query("SELECT * FROM foods WHERE date = :date") // Get foods by date
    fun getFoodsByDate(date: String): List<Food>

    @Query("SELECT * FROM foods WHERE id = :id") // Get food by ID
    fun getFoodById(id: Int): Food?

    @Query("SELECT * FROM foods WHERE date BETWEEN :startDate AND :endDate") // Get foods by date range
    fun getFoodsByDateRange(startDate: String, endDate: String): List<Food>

    @Query("SELECT COUNT(*) FROM foods") // Count total foods
    fun countTotalFoods(): Int

    @Query("DELETE FROM foods WHERE date = :date") // Delete foods by date
    fun deleteFoodsByDate(date: String)

    @Query("SELECT * FROM foods ORDER BY date DESC") // Get foods ordered by date
    fun getFoodsOrderedByDate(): List<Food>
    @Insert
    fun insertFood(food: Food)

    @Update
    fun updateFood(food: Food)

    @Delete
    fun deleteFood(food: Food)
}

// Database class must be an abstract class that extends RoomDatabase
// For each DAO class that is associated with the database, the database class
// must define an abstract method that has zero arguments and returns an
// instance of the DAO class
@Database(entities = [Food::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
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
            ).allowMainThreadQueries() // Using main thread for now
                .build()

            INSTANCE = instance
            instance
        }
    }
}
