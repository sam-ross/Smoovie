import React from "react";
import PulseLoader from "react-spinners/PulseLoader"
import pulpFiction from "../resources/demo/img/tt0110912.webp"
import darkKnight from "../resources/demo/img/tt0468569.webp"
import wolfOfWallStreet from "../resources/demo/img/tt0993846.webp"
import fightClub from "../resources/demo/img/tt0137523.webp"
import shrek2 from "../resources/demo/img/tt0298148.webp"

class MovieList extends React.Component {

  componentDidUpdate(prevProps) {
    if ((prevProps.submitted !== this.props.submitted) || (!prevProps.demoMode && this.props.demoMode)) {
      this.props.getMovieList();
    }
  }

  render() {
    const isLoaded = this.props.isLoaded;
    const movies = this.props.movies;
    const wordDataIsLoading = (this.props.wordDataIsLoaded === 'loading');
    const demoImages = {
      "tt0110912": pulpFiction,
      "tt0468569": darkKnight,
      "tt0993846": wolfOfWallStreet,
      "tt0137523": fightClub,
      "tt0298148": shrek2,
    }

    if (isLoaded !== 'waiting' && isLoaded !== 'loading') {
      let regex = /(?:(?:18|19|20|21)[0-9]{2})/g;   // regex used to extract the movie year from the description
      let displayNoContent = this.props.displayNoContent;
      return (
        <section className="section-movie-list" id="movie-list-id" >
          <h2>Step 2: Select your movie:</h2>
          <ul className="list-movies">
            {movies.map(movie => (
              <li key={movie.id} className="list-item-movies">
                {/* By initially setting the image width and height in html, this means on first calculation,
                the images will be 200x300 then the css property for height: auto will adjust the height of the 
                image to keep it's aspect ratio. This is instead of the height being 0 on first calculation,
                so this allows the scrollToView fuction to work more effectively. */}
                <img
                  src={this.props.demoMode ? demoImages[movie.id] : movie.image}
                  alt={movie.id} onClick={this.props.handleImageClick}
                  className="image-movies"
                  width={200}
                  height={300}
                />
                <p className="movie-title">
                  {movie.title}
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
              loading={(wordDataIsLoading)}
            />
            <p className={"no-content-message-" + displayNoContent}>The OpenSubtitles API doesn't have subtitles for the movie you just selected</p>
          </div>
        </section >
      )
    }
  }

}

export default MovieList;