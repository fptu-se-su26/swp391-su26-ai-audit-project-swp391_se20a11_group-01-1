IF DB_ID('RestaurantDB') IS NULL
BEGIN
    CREATE DATABASE RestaurantDB;
END
GO

USE RestaurantDB;
GO

SELECT DB_NAME() AS current_database;
GO
