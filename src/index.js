import React from 'react';
import ReactDOM from 'react-dom/client';
import MovieList from './components/movieList'
import MovieForm from './components/movieSearch';
import WordList from './components/wordList';
import WordFrequencies from './components/wordFrequencies';
import WordLengths from './components/wordLengths';
import PhraseFrequencies from './components/phraseFrequencies';
import SwearWordFrequencies from './components/swearWordFrequencies';
import SwearWordFrequenciesOverTime from './components/swearWordFrequenciesOverTime';
import Footer from './components/footer';
import Toggle from 'react-toggle';
import demoMovieList from './demo/demo-movie-list.json';
import pulpFictionWords from './demo/pulp-fiction-words.json';
import darkKnightWords from './demo/the-dark-knight-words.json';
import wolfOfWallStreetWords from './demo/the-wolf-of-wall-street-words.json';
import fightClubWords from './demo/fight-club-words.json';
import shrek2Words from './demo/shrek2-words.json';


class MainWrapper extends React.Component {
  constructor() {
    super();
    this.state = {
      baseUrl: "https://smoovie-360607.nw.r.appspot.com/",
      // baseUrl: "https://localhost:8081",

      value: '',
      submitted: false,
      wordCount: null,

      movieListIsLoaded: 'waiting',
      movieListMovies: [],
      moviesWithNoSubtitles: [],
      displayNoContent: false,

      wordListIsLoaded: 'waiting',
      wordListWords: [],

      imdbId: null,
      movieClicked: false,
      wordsRetrieved: false,

      wordDataIsLoaded: 'waiting',
      wordData: {},

      commonRemoved: true,
      sliderCurrentValue: 3,
      sliderMarks: [2, 3, 4, 5, 6, 7, 8],
      sliderMax: 6,

      demoMode: false,
      demoFiles: {
        "tt0110912": pulpFictionWords,
        "tt0468569": darkKnightWords,
        "tt0993846": wolfOfWallStreetWords,
        "tt0137523": fightClubWords,
        "tt0298148": shrek2Words,
      }
    };
  }

  handleChange(e) {
    this.setState({ value: e.target.value });
  }

  handleSubmit(e) {
    if (this.state.value === "") {
      alert("Movie title can't be empty!");
      e.preventDefault();
    } else {
      e.preventDefault();
      this.setState({ submitted: !this.state.submitted });
    }
  }

  handleImageClick(e) {
    e.preventDefault();
    document.getElementById("loading-row").scrollIntoView({ behavior: 'smooth' });

    this.setState({
      imdbId: e.target.alt,
      movieClicked: !this.state.movieClicked,
      displayNoContent: false,
    });
  }

  handleNoSubtitles() {
    let newMovieList = this.state.movieListMovies;
    newMovieList.map((movie) => {
      if (movie.id === this.state.imdbId) {
        movie["title"] = <b>The OpenSubtitles API doesn't have subtitles for this one</b>;
        movie["description"] = "";
      }
    })
    let temp = this.state.moviesWithNoSubtitles;
    temp.push(this.state.imdbId);

    this.setState({
      movieListMovies: newMovieList,
      moviesWithNoSubtitles: temp,
      displayNoContent: true,
    });
  }

  setWordCount(count) {
    this.setState({
      wordCount: count
    })
  }

  handleToggleChange() {
    this.setState({ commonRemoved: !this.state.commonRemoved });
  }

  handleToggleChangeDemo() {
    // hides all the sections again when flicking the toggle EITHER way (needed for scrollIntoView to work)
    this.setState({
      movieListIsLoaded: "waiting",
      wordListIsLoaded: "waiting",
      wordDataIsLoaded: "waiting",
      demoMode: !this.state.demoMode,
    })
  }

  handleSliderChange(e) {
    let newPosition = e + 2;
    this.setState({ sliderCurrentValue: newPosition });
  }

  changeSliderValue() {
    if (window.innerWidth < 705 && this.state.sliderMax !== 3) {
      let sliderValue = this.state.sliderCurrentValue;
      if (this.state.sliderCurrentValue > 5) {
        sliderValue = 5;
      }
      this.setState({
        sliderCurrentValue: sliderValue,
        sliderMarks: [2, 3, 4, 5],
        sliderMax: 3
      })
    } else if (window.innerWidth > 704 && this.state.sliderMax !== 6) {
      this.setState({
        sliderMarks: [2, 3, 4, 5, 6, 7, 8],
        sliderMax: 6
      })
    }
  }

  getMovieList() {
    this.setState({
      movieListIsLoaded: 'loading',
      wordListIsLoaded: 'waiting',
      wordDataIsLoaded: 'waiting',
      moviesWithNoSubtitles: [],
    });

    if (this.state.demoMode) {
      this.setState({
        movieListIsLoaded: 'done',
        movieListMovies: demoMovieList.movies,
      });
    } else {
      fetch(this.state.baseUrl + "/imdb/search/" + this.state.value)
        .then((res) => {
          if (res.status >= 400 || res.status === 204) {
            throw new Error(res.status);
          }
          return res.json();
        })
        .then(
          (result) => {
            this.setState({
              movieListIsLoaded: 'done',
              movieListMovies: result.movies,
            });
          },
          (error) => {
            if (error.message === "406" || error.message === "506" || error.message === "502") {
              window.alert("The external IMDb API seems to be down at the moment: " + error.message + "\nPlease try again. \n\nIf the problem persists, try out the demo mode.");
            } else {
              window.alert("Unexpected error returned: " + error.message);
            }

            this.setState({
              movieListIsLoaded: 'waiting',
            });
          }
        )
    }
  }

  getWordList() {
    let imdbId = this.state.imdbId;
    this.setState({
      wordListIsLoaded: 'loading',
      wordDataIsLoaded: 'waiting',
    });

    if (this.state.demoMode) {
      let demoFiles = this.state.demoFiles;

      this.setState({
        wordListIsLoaded: 'done',
        wordListWords: demoFiles[imdbId].words,
        wordsRetrieved: !this.state.wordsRetrieved,
      });
    } else {
      fetch(this.state.baseUrl + "/words/" + imdbId)
        .then((res) => {
          if (res.status >= 400 || res.status === 204) {
            throw new Error(res.status);
          }
          return res.json();
        })
        .then(
          (result) => {
            this.setState({
              wordListIsLoaded: 'done',
              wordListWords: result.words,
              wordsRetrieved: !this.state.wordsRetrieved,
            });
          },
          (error) => {
            if (error.message === "204") {
              this.handleNoSubtitles();
            } else if (error.message === "406" || error.message === "506" || error.message === "502") {
              window.alert("The external OpenSubtitles API seems to be down at the moment: " + error.message + "\nPlease try again. \n\nIf the problem persists, try out the demo mode.");
            } else {
              window.alert("Unexpected error returned: " + error.message);
            }
            this.setState({
              wordListIsLoaded: 'waiting',
            });
          }
        )
    }
  }

  getWordData() {
    this.setState({ wordDataIsLoaded: 'loading' });

    fetch(this.state.baseUrl + "/words/data", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(this.state.wordListWords),
    })
      .then((res) => {
        if (res.status >= 400 || res.status === 204) {
          throw new Error(res.status);
        }
        return res.json();
      })
      .then(
        (result) => {
          this.setState({
            wordDataIsLoaded: 'done',
            wordData: result,
          });
        },
        (error) => {
          window.alert("Unexpected error returned: " + error.message);
          this.setState({
            wordDataIsLoaded: 'waiting',
          });
        }
      )

  }

  render() {
    return (
      <div>
        <div className='gradient-background-container'>
          <div className='header-and-first-section'>
            <div className="header">
              <a className='smoovie-title' href=".">Smoovie</a>
              <div className="header-nav" id={"header-nav-" + this.state.demoMode} onClick={() => this.handleToggleChangeDemo()}>
                <p>Demo Mode</p>
                <div className="toggle-center">
                  <Toggle
                    checked={this.state.demoMode}
                    icons={false}
                    onChange={() => this.handleToggleChangeDemo()}
                  />

                </div>
              </div>
            </div>

            <div className="search">
              <h1>Subtitle moovie analyser</h1>
              <p className="under-heading">Step 1: Search for a moovie</p>

              <div className='input-container'>
                <MovieForm
                  value={this.state.value}
                  handleChange={(e) => this.handleChange(e)}
                  handeSubmit={(e) => this.handleSubmit(e)}
                  movieListIsLoaded={this.state.movieListIsLoaded}
                  demoMode={this.state.demoMode}
                />
              </div>

            </div>
          </div>
        </div>

        <div className='container-light-grey'>
          <MovieList
            submitted={this.state.submitted}
            getMovieList={() => this.getMovieList()}
            handleImageClick={(e) => this.handleImageClick(e)}
            wordListIsLoaded={this.state.wordListIsLoaded}
            wordDataIsLoaded={this.state.wordDataIsLoaded}

            isLoaded={this.state.movieListIsLoaded}
            movies={this.state.movieListMovies}

            demoMode={this.state.demoMode}
            displayNoContent={this.state.displayNoContent}
          />
        </div>

        <WordList
          getWordList={() => this.getWordList()}
          setWordCount={(count) => this.setWordCount(count)}
          clicked={this.state.movieClicked}

          isLoaded={this.state.wordListIsLoaded}
          words={this.state.wordListWords}

          movieListIsLoaded={this.state.movieListIsLoaded}

          handleNoSubtitles={() => this.handleNoSubtitles()}
          imdbId={this.state.imdbId}
          moviesWithNoSubtitles={this.state.moviesWithNoSubtitles}

          demoMode={this.state.demoMode}
        />

        <WordFrequencies
          getWordData={() => this.getWordData()}
          wordsRetrieved={this.state.wordsRetrieved}
          wordCount={this.state.wordCount}

          isLoaded={this.state.wordDataIsLoaded}
          words={this.state.wordData.wordFrequencies}
          wordsAll={this.state.wordData.wordFrequenciesWithCommonWords}
          commonRemoved={this.state.commonRemoved}

          defaultChecked={this.state.commonRemoved}
          icons={false}
          onChange={() => this.handleToggleChange()}
        />

        <div className='container-dark-grey'>
          <WordLengths
            isLoaded={this.state.wordDataIsLoaded}
            wordLengths={this.state.wordData.wordLengths}
          />
        </div>

        <PhraseFrequencies
          isLoaded={this.state.wordDataIsLoaded}
          phraseFrequencyRanges={this.state.wordData.phraseFrequencyRanges}
          sliderCurrentValue={this.state.sliderCurrentValue}
          onChange={(e) => this.handleSliderChange(e)}

          changeSliderValue={() => this.changeSliderValue()}
          sliderMarks={this.state.sliderMarks}
          sliderMax={this.state.sliderMax}
        />

        <div className='container-light-grey'>
          <SwearWordFrequencies
            isLoaded={this.state.wordDataIsLoaded}
            swearWordFrequencies={this.state.wordData.swearWordFrequencies}
          />
        </div>

        <div className='container-dark-grey'>
          <SwearWordFrequenciesOverTime
            isLoaded={this.state.wordDataIsLoaded}
            swearWordFrequenciesOverTime={this.state.wordData.swearWordFrequenciesOverTime}
            demoMode={this.state.demoMode}
          />
        </div>

        <div className='gradient-background-container-footer'>
          <Footer
            isLoaded={this.state.wordDataIsLoaded}
          />
        </div>
      </div >
    );
  }

}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <div>
    <MainWrapper />
  </div>
);
