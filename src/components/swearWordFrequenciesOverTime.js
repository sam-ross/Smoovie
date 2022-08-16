import React from "react";
import { Line } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

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

      let labels = Object.keys(swearWordFrequenciesOverTime);
      let values = Object.values(swearWordFrequenciesOverTime);

      let chartData = {
        labels: labels,
        datasets: [{
          label: "Swear word frequency",
          data: values,
          borderColor: 'rgb(75, 192, 192)',
          backgroundColor: 'rgb(75, 192, 192, 0.2)',
          pointBackgroundColor: 'rgb(75, 192, 192)',
          pointBorderColor: 'rgb(255,255,255)',
          // hoverBackgroundColor: 'rgb(255,255,255)',
          // pointHoverBackgroundColor: 'rgb(255,255,255)',
          hoverRadius: 6,
          hoverBorderWidth: 1,
          pointBorderWidth: 0,
          pointRadius: 3,
          cubicInterpolationMode: 'monotone',
          fill: true,
          xAxisId: "x123"
        }]
      }

      let options = {
        scales: {
          x: {
            title: {
              display: true,
              text: 'Sections'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Frequency'
            }
          },
        },
        plugins: {
          legend: {
            display: false
          }
        },
      }

      return (
        <div>
          <Line data={chartData} options={options} />
        </div>
      )

    }
  }

}

export default SwearWordFrequenciesOverTime;