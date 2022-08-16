import React from "react";

class WordLengths extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const wordLengths = this.props.wordLengths;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      console.log(wordLengths);

      let entries = Object.entries(wordLengths).sort((a, b) => b[1] - a[1]);
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

export default WordLengths;