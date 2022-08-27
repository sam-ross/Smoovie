import React from "react";

class WordList extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.clicked !== this.props.clicked) {
      this.props.getWordList();
    }
    if (prevProps.words.length !== this.props.words.length) {
      this.props.setWordCount(this.props.words.length);
    }

    // if the movie list has successfully finished loading, then scroll down to the list
    if ((prevProps.movieListIsLoaded === 'loading' || prevProps.movieListIsLoaded === 'waiting') && this.props.movieListIsLoaded === 'done') {
      if (this.props.demoMode) {
        setTimeout(function () {
          document.getElementById("movie-list-id").scrollIntoView({ behavior: 'smooth' });
        }, 300);
      } else {
        document.getElementById("movie-list-id").scrollIntoView({ behavior: 'smooth' });
      }
    }
  }

  render() {
    const isLoaded = this.props.isLoaded;
    if (isLoaded !== 'waiting' && isLoaded !== 'loading') {
      return (
        <></>
      )
    }
  }

}

export default WordList;