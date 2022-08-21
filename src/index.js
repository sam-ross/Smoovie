import React, { Component } from 'react';
import ReactDOM from 'react-dom/client';
import MovieList from './components/movieList';
import MovieForm from './components/movieSearch';
import WordList from './components/wordList';
import WordFrequencies from './components/wordFrequencies';
import WordLengths from './components/wordLengths';
import PhraseFrequencies from './components/phraseFrequencies';
import SwearWordFrequencies from './components/swearWordFrequencies';
import SwearWordFrequenciesOverTime from './components/swearWordFrequenciesOverTime';
import Toggle from 'react-toggle'
import pulpFictionMovie from './demo/pulp-fiction.json';
import pulpFictionWords from './demo/pulp-fiction-words.json';
import pulpFictionData from './demo/pulp-fiction-data.json'

class MainWrapper extends React.Component {
  constructor() {
    super();
    this.state = {
      value: '',
      submitted: false,
      wordCount: null,

      movieListIsLoaded: 'waiting',
      movieListMovies: [],
      moviesWithNoSubtitles: [],

      wordListIsLoaded: 'waiting',
      wordListWords: [],

      imdbId: null,
      movieClicked: false,
      wordsRetrieved: false,

      wordDataIsLoaded: 'waiting',
      wordData: {},

      commonRemoved: true,
      sliderCurrentValue: 3,
      demoMode: false
    };
  }

  handleChange(e) {
    this.setState({ value: e.target.value });
    // console.log(this.state.value);
    // console.log(e.target.value);
    console.log(this.state.submitted + "   " + this.state.value);
  }

  handleSubmit(e) {
    if (this.state.value === "") {
      alert("Movie title can't be empty!");
      e.preventDefault();
    } else {
      console.log("value:" + this.state.value)
      e.preventDefault();
      this.setState({ submitted: !this.state.submitted });
    }
  }

  handleImageClick(e) {
    console.log(e.target.alt);
    console.log(this.state.value);

    e.preventDefault();

    this.setState({
      imdbId: e.target.alt,
      movieClicked: !this.state.movieClicked
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
    });
  }

  setWordCount(count) {
    console.log(count);
    this.setState({
      wordCount: count
    })
  }

  handleToggleChange() {
    console.log("Toggle changed from: " + this.state.commonRemoved + " to " + !this.state.commonRemoved);
    this.setState({ commonRemoved: !this.state.commonRemoved });
  }

  handleToggleChangeDemo() {
    console.log("Demo mode changed from: " + this.state.demoMode + " to " + !this.state.demoMode);

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
    console.log("Moving slider from :" + this.state.sliderCurrentValue + " to " + newPosition);
    this.setState({ sliderCurrentValue: newPosition });
  }



  getMovieList() {
    console.log("Getting movie list (parent): " + this.state.value);
    console.log("Demo mode: " + this.state.demoMode);
    this.setState({
      movieListIsLoaded: 'loading',
      wordListIsLoaded: 'waiting',
      wordDataIsLoaded: 'waiting',
      moviesWithNoSubtitles: [],
    });

    if (this.state.demoMode) {
      console.log("In demo mode");
      console.log(pulpFictionMovie.movies);

      this.setState({
        movieListIsLoaded: 'done',
        movieListMovies: pulpFictionMovie.movies,
      });
    } else {
      fetch("http://localhost:8081/imdb/search/" + this.state.value)
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
            console.log(error);
            if (error.message === "204") {
              window.alert('No results found for "' + this.state.value + '"');
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
    console.log("Getting word list (parent): " + this.state.imdbId);
    this.setState({
      wordListIsLoaded: 'loading',
      wordDataIsLoaded: 'waiting',
    });

    if (this.state.demoMode) {
      console.log("In demo mode");
      console.log(pulpFictionWords.words);

      this.setState({
        wordListIsLoaded: 'done',
        wordListWords: pulpFictionWords.words,
        wordsRetrieved: !this.state.wordsRetrieved,
      });
    } else {
      fetch("http://localhost:8081/words/" + this.state.imdbId)
        .then((res) => {
          if (res.status >= 400 || res.status === 204) {
            throw new Error(res.status);
          }
          return res.json();
        })
        .then(
          (result) => {
            console.log(result);
            this.setState({
              wordListIsLoaded: 'done',
              wordListWords: result.words,
              wordsRetrieved: !this.state.wordsRetrieved,
            });
          },
          (error) => {
            console.log(error);
            if (error.message === "204") {
              this.handleNoSubtitles();
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
    console.log("Getting word data (parent)");
    this.setState({ wordDataIsLoaded: 'loading' });

    if (this.state.demoMode) {
      console.log("In demo mode");
      console.log(pulpFictionData);

      this.setState({
        wordDataIsLoaded: 'done',
        wordData: pulpFictionData,
      });
    } else {
      fetch("http://localhost:8081/words/data", {
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
            console.log(result);
            this.setState({
              wordDataIsLoaded: 'done',
              wordData: result,
            });
          },
          (error) => {
            console.log(error);
            window.alert("Unexpected error returned: " + error.message);
            this.setState({
              wordDataIsLoaded: 'waiting',
            });
          }
        )
    }

  }

  render() {
    return (

      <div>
        <div className='gradient-background-container'>
          <div className='header-and-first-section'>
            <div className="header">
              <p className='smoovie-title'>Smoovie</p>
              <ul className="header-nav">
                <li >
                  <a href='' >Documentation</a>
                </li>
                <li className='demo-mode'>
                  <a>Demo Mode</a>

                  <Toggle
                    defaultChecked={this.state.demoMode}
                    icons={false}
                    onChange={() => this.handleToggleChangeDemo()} />
                </li>
              </ul>

            </div>

            <div className="search">
              <h1>Subtitle moovie analyser</h1>
              <p className="under-heading">Step 1: Search for a moovie!</p>

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

        />

        <WordFrequencies
          getWordData={() => this.getWordData()}
          wordsRetrieved={this.state.wordsRetrieved}
          wordCount={this.state.wordCount}

          isLoaded={this.state.wordDataIsLoaded}
          words={this.state.wordData.wordFrequencies}
          wordsAll={this.state.wordData.wordFrequenciesWithCommonWords}
          commonRemoved={this.state.commonRemoved}

          // Toggle Props
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
