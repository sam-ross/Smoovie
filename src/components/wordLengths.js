import React from "react";
import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

class WordLengths extends React.Component {
  render() {
    const error = this.props.error;
    const isLoaded = this.props.isLoaded;
    const wordLengths = this.props.wordLengths;
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
      return <div>Loading...</div>
    } else {
      console.log(wordLengths);

      // set up chart data
      let labels = Object.keys(wordLengths);
      let values = Object.values(wordLengths);

      let chartData = {
        labels: labels.map((label) => label + " letter words"),
        datasets: [{
          label: "Word lengths",
          data: values,
          backgroundColor: colours,
          hoverOffset: 15,
        }]
      }

      let options = {
        responsive: true,
        plugins: {
          legend: {
            position: 'right',
          }
        }
      }

      // return chart
      return (
        <div className="chart-outer">
          <div className="chart">
            <Doughnut data={chartData} options={options} />
          </div>
        </div>
      )

    }
  }

}

export default WordLengths;