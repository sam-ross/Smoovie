import React from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

class SwearWordFrequencies extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const swearWordFrequencies = this.props.swearWordFrequencies;
    const colours = [
      'rgb(255, 99, 132)',
      'rgb(255, 159, 64)',
      'rgb(255, 205, 86)',
      'rgb(75, 192, 192)',
      'rgb(54, 162, 235)',
      'rgb(153, 102, 255)',
    ]

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      // return <div>Loading...</div>
    } else {
      console.log(swearWordFrequencies);
      // set up chart data
      let labels = Object.keys(swearWordFrequencies);
      let values = Object.values(swearWordFrequencies);

      let chartData = {
        labels: labels,
        datasets: [{
          label: "Swear word frequencies",
          data: values,
          backgroundColor: colours,
          hoverOffset: 20,
        }]
      }

      let options = {
        responsive: true,
        plugins: {
          legend: {
            // display: false,
            position: 'right'
          },
          // title: {
          //   display: true,
          //   text: 'Count of different word lengths'
          // }
        }
      }

      function swearWordsFunc() {
        if (Object.keys(swearWordFrequencies).length !== 0) {
          return <Pie data={chartData} options={options} />;
        }
        return <h2 className="swear-word-no">* No swear words in this movie! *</h2 >
      }


      return (
        <div className='section-words-swear'>
          <h2>Swear Word Frequencies</h2>
          <div className="chart-outer">
            <div className="chart">
              {swearWordsFunc()}
            </div>
          </div>
        </div>
      )

    }
  }

}

export default SwearWordFrequencies;