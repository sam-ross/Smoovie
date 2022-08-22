import React from "react";
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
    const isLoaded = this.props.isLoaded;
    const movies = this.props.movies;
    const wordListIsLoading = (this.props.wordListIsLoaded === 'loading');
    const wordDataIsLoading = (this.props.wordDataIsLoaded === 'loading');

    if (isLoaded === 'waiting') {
      console.log("waiting (movieList)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (movieList)");
    } else {
      console.log("done (movieList)")
      return (
        <section className="section-movie-list" id="movie-list-id" >
          <h2>Step 2: Select your movie:</h2>
          <ul className="list-movies">
            {movies.map(movie => (
              <li key={movie.id} className="list-item-movies">
                {/* By initially setting the image width and height in the html, this means on first calculation,
                the images will be 200x300 then the css property for height auto will adjust the height of the 
                image to keep it's aspect ratio. This is instead of the height being 0 on first calculation,
                so this allows the scrollToView fuction to work more effectively. */}
                <img src={movie.image} alt={movie.id} onClick={this.props.handleImageClick} className="image-movies" width={200} height={300}></img>
                <p className="movie-title">{movie.title} <br />{movie.description.substring(0, 6)}</p>
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
        </section >
      )
    }
  }

}

export default MovieList;