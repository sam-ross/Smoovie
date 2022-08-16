import React from "react";

class SwearWordFrequenciesOverTime extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const swearWordFrequenciesOverTime = this.props.swearWordFrequenciesOverTime;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      console.log(swearWordFrequenciesOverTime);

      let entries = Object.entries(swearWordFrequenciesOverTime);
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

export default SwearWordFrequenciesOverTime;