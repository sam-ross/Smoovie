import React from "react";

class WordFrequencies extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.wordsRetrieved !== this.props.wordsRetrieved) {
      console.log("Calling API from child (getWordData) (for frequency)");
      this.props.getWordData();
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
      console.log(words);

      let entries = Object.entries(words).sort((a, b) => b[1] - a[1]).slice(0, 30);
      return (
        <ul className="word-frequency-list-container">
          {entries.map(entry => (

            <li className="word-frequency-li" key={entry}><div>{entry[0]}</div> <div>{entry[1]}</div></li>
          ))}
        </ul>
      )

    }
  }

}

export default WordFrequencies;