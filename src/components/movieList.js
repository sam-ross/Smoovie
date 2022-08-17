import React from "react";
import dashedBox from '../images/dashedBox.png'

class MovieList extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.submitted !== this.props.submitted) {
      console.log("Calling API from child (getMovieList)");
      console.log(this.props.error);
      this.props.getMovieList();
    }

  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const movies = this.props.movies;
    const arr = [1, 2, 3, 4, 5]

    if (error) {
      return <div>
        Error: {error.message}
        <br />API: IMDb API
      </div>
    } else if (!isLoaded) {
      // return (
      // <ul className="list-movies">
      //   {arr.map(id => (
      //     <li key={id} className="list-item-movies">
      //       <img src={dashedBox} alt={"box"} className="image-movies"></img>
      //     </li>
      //   ))
      //   }
      // </ul >
      // )
    } else {
      return (
        <div className='section-movie-list'>
          <h2>Step 2: Select your movie:</h2>
          <ul className="list-movies">
            {movies.map(movie => (
              <li key={movie.id} className="list-item-movies">
                <img src={movie.image} alt={movie.id} onClick={this.props.handleImageClick} className="image-movies"></img>
                {movie.title} - {movie.description.substring(0, 6)}
              </li>
            ))
            }
          </ul >
        </div>
      )
    }
  }

}
/* Now get it to call then new endpoint with the correct imdbid! */

export default MovieList;