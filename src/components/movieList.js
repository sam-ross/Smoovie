import React from "react";

class MovieList extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.submitted !== this.props.submitted) {
      console.log("Calling API from child (getMovieList)");
      this.props.getMovieList();
    }

  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const movies = this.props.movies;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      return (
        <ul className="list-movies">
          {movies.map(movie => (
            <li key={movie.id} className="list-item-movies">
              <img src={movie.image} alt={movie.id} onClick={this.props.handleImageClick} className="image-movies"></img>
              {movie.title} - {movie.description.substring(0, 6)}
            </li>
          ))
          }
        </ul >
      )
    }
  }

}
/* Now get it to call then new endpoint with the correct imdbid! */

export default MovieList;