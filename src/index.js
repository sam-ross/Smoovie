import React, { Component } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import MovieList from './components/movieList';
import MovieForm from './components/movieSearch';
import WordList from './components/wordList';
import WordFrequencies from './components/wordFrequencies';
import WordLengths from './components/wordLengths';
import PhraseFrequencies from './components/phraseFrequencies';
import SwearWordFrequencies from './components/swearWordFrequencies';
import SwearWordFrequenciesOverTime from './components/swearWordFrequenciesOverTime';



class MainWrapper extends React.Component {
  constructor() {
    super();
    this.state = {
      value: '',
      submitted: false,
      wordCount: null,

      movieListError: null,
      movieListIsLoaded: false,
      movieListMovies: [],

      wordListError: null,
      wordListIsLoaded: false,
      wordListWords: [],

      imdbId: null,
      movieClicked: false,
      wordsRetrieved: false,

      wordDataError: null,
      wordDataIsLoaded: false,
      wordData: {},

      commonRemoved: true,
      sliderCurrentValue: 3
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

  handleSliderChange(e) {
    let newPosition = e + 2;
    console.log("Moving slider from :" + this.state.sliderCurrentValue + " to " + newPosition);
    this.setState({ sliderCurrentValue: newPosition });
  }

  getMovieList() {
    console.log("Getting movie list (parent): " + this.state.value);

    fetch("http://localhost:8081/imdb/search/" + this.state.value)
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            movieListIsLoaded: true,
            movieListMovies: result.movies,
            movieListError: null
          });
        },
        (error) => {
          this.setState({
            movieListIsLoaded: true,
            movieListError: error
          });
        }
      )

  }

  getWordList() {
    console.log("Getting word list (parent): " + this.state.imdbId);

    fetch("http://localhost:8081/words/" + this.state.imdbId)
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            wordListIsLoaded: true,
            wordListWords: result.words,
            wordsRetrieved: !this.state.wordsRetrieved,
            wordListError: null,
          });
        },
        (error) => {
          console.log(error);
          this.setState({
            wordListIsLoaded: true,
            wordListError: error,
          });
        }
      )

  }

  getWordData() {
    console.log("Getting word data (parent)");
    console.log(JSON.stringify(this.state.wordListWords));

    fetch("http://localhost:8081/words/data", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(this.state.wordListWords),
    })
      .then(res => res.json())
      .then(
        (result) => {
          console.log(result);
          this.setState({
            wordDataIsLoaded: true,
            wordData: result,
            wordDataError: null
          });
        },
        (error) => {
          this.setState({
            wordDataIsLoaded: true,
            wordDataError: error
          });
        }
      )

  }

  render() {
    return (

      <div>
        <div className='header-and-first-section'>
          <header class="header">
            <h1 class="header1">Smoovie - Subtitle Movie Analyser</h1>
          </header>
          <div className='section-form'>
            <h2>Step 1: Search for any movie:</h2>
            <MovieForm
              value={this.state.value}
              handleChange={(e) => this.handleChange(e)}
              handeSubmit={(e) => this.handleSubmit(e)}
            />
          </div>
        </div>



        <MovieList
          submitted={this.state.submitted}
          getMovieList={() => this.getMovieList()}
          handleImageClick={(e) => this.handleImageClick(e)}

          error={this.state.movieListError}
          isLoaded={this.state.movieListIsLoaded}
          movies={this.state.movieListMovies}
        />



        <WordList
          getWordList={() => this.getWordList()}
          setWordCount={(count) => this.setWordCount(count)}
          clicked={this.state.movieClicked}

          error={this.state.wordListError}
          isLoaded={this.state.wordListIsLoaded}
          words={this.state.wordListWords}
        />

        <WordFrequencies
          getWordData={() => this.getWordData()}
          wordsRetrieved={this.state.wordsRetrieved}
          wordCount={this.state.wordCount}

          error={this.state.wordDataError}
          isLoaded={this.state.wordDataIsLoaded}
          words={this.state.wordData.wordFrequencies}
          wordsAll={this.state.wordData.wordFrequenciesWithCommonWords}
          commonRemoved={this.state.commonRemoved}

          // Toggle Props
          defaultChecked={this.state.commonRemoved}
          icons={false}
          onChange={() => this.handleToggleChange()}
        />

        <WordLengths
          error={this.state.wordDataError}
          isLoaded={this.state.wordDataIsLoaded}
          wordLengths={this.state.wordData.wordLengths}
        />

        <PhraseFrequencies
          error={this.state.wordDataError}
          isLoaded={this.state.wordDataIsLoaded}
          phraseFrequencyRanges={this.state.wordData.phraseFrequencyRanges}
          sliderCurrentValue={this.state.sliderCurrentValue}
          onChange={(e) => this.handleSliderChange(e)}
        />

        <SwearWordFrequencies
          error={this.state.wordDataError}
          isLoaded={this.state.wordDataIsLoaded}
          swearWordFrequencies={this.state.wordData.swearWordFrequencies}
        />

        <SwearWordFrequenciesOverTime
          error={this.state.wordDataError}
          isLoaded={this.state.wordDataIsLoaded}
          swearWordFrequenciesOverTime={this.state.wordData.swearWordFrequenciesOverTime}
        />
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
