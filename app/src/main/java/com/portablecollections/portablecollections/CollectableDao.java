package com.portablecollections.portablecollections;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;


@Dao
public interface CollectableDao {
    /**
     * Counts the number of collectables in the table
     *
     * @return The number of collectables.
     */

    @Query("SELECT COUNT (*) FROM collectables")
    int count();

    /**
     * Inserts a single collectable in the database.
     *
     * @param collectable the collectable to be inserted.
     * @return the row ID of the inserted collectable.
     */
    @Insert
    int insert(Collectable collectable);

    /**
     * Inserts multiple collectables into the table
     *
     * @param collectables an array of collectables.
     * @return the row ID's of the inserted collectables.
     */
    @Insert
    int[] insertAll(Collectable[] collectables);

    /**
     * Select all collectables.
     *
     * @return A {@link Cursor} of the collectables in the table.
     */
    @Query("SELECT name, description, country, city FROM collectables")
    Cursor selectAll();

    /**
     * Deletes a single collectable from the database.
     *
     * @param id the id of the record that is to be deleted.
     * @return the number of collectables deleted. This should always be {@code 1}.
     */
    @Query("DELETE FROM collectables WHERE id = :id")
    int deleteById(int id);

    /**
     * Update the collectable identified by the row ID.
     *
     * @param collectable the collectable to be deleted.
     * @return the number of collectables updated. This should always be {@code 1}.
     */
    @Update
    int update(Collectable collectable);

}