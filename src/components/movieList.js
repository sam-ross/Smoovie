import React from "react";
import ReactDOM from 'react-dom/client';
import PulseLoader from "react-spinners/PulseLoader"

class MovieList extends React.Component {

  componentDidMount() {
    console.log("Movie list mounted");
  }

  componentDidUpdate(prevProps) {
    if (prevProps.submitted !== this.props.submitted) {
      console.log("Calling API from child (getMovieList)");
      this.props.getMovieList();
    }
    if (prevProps.demoMode === false && this.props.demoMode === true) {
      console.log("Calling API from child (getMovieList) [demo mode true]");
      this.props.getMovieList();
    }
  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const movies = this.props.movies;
    const wordListIsLoading = (this.props.wordListIsLoaded === 'loading');
    const wordDataIsLoading = (this.props.wordDataIsLoaded === 'loading');

    if (error) {
      return <div>
        Error: {error.message}
        <br />API: IMDb API
      </div>
    } else if (isLoaded === 'waiting') {
      console.log("waiting (movieList)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (movieList)");
    } else {
      console.log("done...")
      return (
        < div className='section-movie-list' id="movie-list-id" >
          <h2>Step 2: Select your movie:</h2>
          <ul className="list-movies">
            {movies.map(movie => (
              <li key={movie.id} className="list-item-movies">
                <img src={movie.image} alt={movie.id} onClick={this.props.handleImageClick} className="image-movies"></img>
                <p className="movie-title">{movie.title} - {movie.description.substring(0, 6)}</p>
              </li>
            ))
            }
          </ul >
          <div className="loading-row">
            <PulseLoader
              size={15}
              color={"#bf004a"}
              loading={(wordListIsLoading || wordDataIsLoading)}
            />
          </div>
        </div >
      )
    }
  }

}
/* Now get it to call then new endpoint with the correct imdbid! */

export default MovieList;