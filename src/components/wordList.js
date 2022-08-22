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
    if ((prevProps.movieListIsLoaded === 'loading' || prevProps.movieListIsLoaded === 'waiting') && this.props.movieListIsLoaded === 'done') {
      console.log("Wow - movie list rendered");
      document.getElementById("movie-list-id").scrollIntoView({ behavior: 'smooth' });
    }
  }

  render() {
    const isLoaded = this.props.isLoaded;

    if (isLoaded === 'waiting') {
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