import React from "react";

class PhraseFrequencies extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const phraseFrequencies = this.props.phraseFrequencies;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      console.log(phraseFrequencies);

      let entries = Object.entries(phraseFrequencies).slice(0, 30);
      return (
        <ul className="phrase-frequency-list-container">
          {entries.map(entry => (

            <li className="word-frequency-li" key={entry}><div>{entry[0]}</div> <div>{entry[1]}</div></li>
          ))}
        </ul>
      )

    }
  }

}

export default PhraseFrequencies;