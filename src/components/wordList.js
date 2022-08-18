import React from "react";

class WordList extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.clicked !== this.props.clicked) {
      console.log("Calling API from child (getWordList)");
      this.props.getWordList();
    }
    if (prevProps.words.length !== this.props.words.length) {
      this.props.setWordCount(this.props.words.length);
    }
    if (prevProps.movieListHasLoadedFirstTime === false && this.props.movieListHasLoadedFirstTime === true) {
      console.log("Wow - movie list rendered");
      setTimeout(function () {
        document.getElementById("movie-list-id").scrollIntoView({ behavior: 'smooth' });
      }, 400);
    }
  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;

    if (error) {
      return <div className="word-count">
        Error: {error.message}
        <br />API: OpenSubtitles API
      </div>
    } else if (isLoaded === 'waiting') {
      console.log("waiting (wordList)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (wordList)");
    } else {

      return (
        <></>
      )
    }
  }

}

export default WordList;