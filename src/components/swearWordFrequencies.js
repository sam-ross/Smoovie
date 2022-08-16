import React from "react";

class SwearWordFrequencies extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const swearWordFrequencies = this.props.swearWordFrequencies;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      console.log(swearWordFrequencies);

      let entries = Object.entries(swearWordFrequencies);
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

export default SwearWordFrequencies;