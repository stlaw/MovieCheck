MovieCheck
==========
####Introduction

This mobile application will take a movie entry and return critic reviews from 3 major aggregate review sites: Rotten Tomatoes, IMDb, and Metacritic.

####APIs

This application uses APIs from Rotten Tomatoes, Open Movie Database (OMDb), and The Movie Database (TMDb).

####Process

The intial search parses the Rotten Tomatoes' movie search API and retrieves the top 4 results for the title searched. The user can then select the movie of choice, which then takes the IMDB ID and runs a search through the OMDb API for movie info and ratings, and the TMDb API for the movie images. Then the ratings are posted.

