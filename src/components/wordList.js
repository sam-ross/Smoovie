import React from "react";

class WordList extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.clicked !== this.props.clicked) {
      console.log("Calling API from child (getWordList)");
      this.props.getWordList();
    }
  }

  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const words = this.props.words;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      return (
        <ul>
          {/* {words.map(word => (
            <li key={word}>
              {word}
            </li>
          ))} */}
          {words.length}
        </ul>
      )
    }
  }

}

export default WordList;