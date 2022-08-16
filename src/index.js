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
      wordData: {}
    };
  }

  handleChange(e) {
    this.setState({ value: e.target.value });
    // console.log(this.state.value);
    // console.log(e.target.value);
    console.log(this.state.submitted + "   " + this.state.value);
  }

  handleSubmit(e) {
    alert('A movie name was submitted: ' + this.state.value);
    e.preventDefault();
    this.setState({ submitted: !this.state.submitted });
  }

  handleImageClick(e) {
    // e.preventDefault();
    console.log(e.target.alt);
    console.log(this.state.value);
    // this.setState({ curImdbId: imdbId })
    // this.getWordList(e.target.alt);

    e.preventDefault();
    this.setState({
      imdbId: e.target.alt,
      movieClicked: !this.state.movieClicked
    });
  }

  getMovieList() {
    console.log("Getting movie list (parent): " + this.state.value);

    fetch("http://localhost:8081/imdb/search/" + this.state.value)
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            movieListIsLoaded: true,
            movieListMovies: result.movies
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
          });
        },
        (error) => {
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
            wordData: result
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
        <div className='section-form'>
          <MovieForm
            value={this.state.value}
            handleChange={(e) => this.handleChange(e)}
            handeSubmit={(e) => this.handleSubmit(e)}
          />
        </div>

        <div className='section-movie-list'>
          <h2>Select your movie:</h2>
          <MovieList
            submitted={this.state.submitted}
            getMovieList={() => this.getMovieList()}
            handleImageClick={(e) => this.handleImageClick(e)}

            error={this.state.movieListError}
            isLoaded={this.state.movieListIsLoaded}
            movies={this.state.movieListMovies}
          />
        </div>

        <div className='section-words-list'>
          <h2>Word list</h2>
          <WordList
            getWordList={() => this.getWordList()}
            clicked={this.state.movieClicked}

            error={this.state.wordListError}
            isLoaded={this.state.wordListIsLoaded}
            words={this.state.wordListWords}
          />
        </div>

        <div className='section-words-frequency'>
          <h2>Word Frequencies</h2>
          <WordFrequencies
            getWordData={() => this.getWordData()}
            wordsRetrieved={this.state.wordsRetrieved}

            error={this.state.wordDataError}
            isLoaded={this.state.wordDataIsLoaded}
            words={this.state.wordData.wordFrequencies}
            wordsAll={this.state.wordData.wordFrequenciesWithCommonWords}
          />
        </div>

        <div className='section-words-lengths'>
          <h2>Word Lengths</h2>
          <WordLengths
            error={this.state.wordDataError}
            isLoaded={this.state.wordDataIsLoaded}
            wordLengths={this.state.wordData.wordLengths}
          />
        </div>

        <div className='section-phrase-frequency'>
          <h2>Phrase Frequencies</h2>
          <PhraseFrequencies
            error={this.state.wordDataError}
            isLoaded={this.state.wordDataIsLoaded}
            phraseFrequencies={this.state.wordData.phraseFrequencies}
          />
        </div>

        <div className='section-words-swear'>
          <h2>Swear Word Frequencies</h2>
          <SwearWordFrequencies
            error={this.state.wordDataError}
            isLoaded={this.state.wordDataIsLoaded}
            swearWordFrequencies={this.state.wordData.swearWordFrequencies}
          />
        </div>

        <div className='section-words-swear-time'>
          <h2>Swear Word Frequencies Throughout the Movie</h2>
          <SwearWordFrequenciesOverTime
            error={this.state.wordDataError}
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
