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
  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const words = this.props.words;

    if (error) {
      return <div className="word-count">
        Error: {error.message}
        <br />API: OpenSubtitles API
      </div>
    } else if (!isLoaded) {
      // return <div className="word-count">Loading...</div>
    } else {

      return (
        <></>
      )
    }
  }

}

export default WordList;