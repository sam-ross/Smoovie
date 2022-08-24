import React from "react";
import PulseLoader from "react-spinners/PulseLoader"
import pulpFiction from "../demo/img/tt0110912.jpg"
import darkKnight from "../demo/img/tt0468569.jpg"
import wolfOfWallStreet from "../demo/img/tt0993846.jpg"
import fightClub from "../demo/img/tt0137523.jpg"
import shrek2 from "../demo/img/tt0298148.jpg"

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
    const demoImages = {
      "tt0110912": pulpFiction,
      "tt0468569": darkKnight,
      "tt0993846": wolfOfWallStreet,
      "tt0137523": fightClub,
      "tt0298148": shrek2,
    }

    if (isLoaded === 'waiting') {
      console.log("waiting (movieList)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (movieList)");
    } else {
      console.log("done (movieList)")
      let regex = /(?:(?:18|19|20|21)[0-9]{2})/g;   // regex used to extract the movie year from the description
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
                <img
                  src={this.props.demoMode ? demoImages[movie.id] : movie.image}
                  alt={movie.id} onClick={this.props.handleImageClick}
                  className="image-movies"
                  width={200}
                  height={300}
                ></img>
                <p
                  className="movie-title">{movie.title}
                  <br /> {movie.description.match(regex) ? "(" + movie.description.match(regex)[0] + ")" : null}
                </p>
              </li>
            ))
            }
          </ul >
          <div className="loading-row" id="loading-row">
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