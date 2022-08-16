import React from "react";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

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
    const wordsAll = this.props.wordsAll;

    if (error) {
      return <div> Error: {error.message}</div>
    } else if (!isLoaded) {
      return <div>Loading...</div>
    } else {
      console.log(words);

      let entries = Object.entries(words).sort((a, b) => b[1] - a[1]).slice(0, 50);
      let labels = entries.map((entry) => entry[0]);
      let values = entries.map((entry) => entry[1]);

      let chartData = {
        labels: labels,
        datasets: [{
          label: "Word frequency",
          data: values,
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(255, 159, 64)',
            'rgb(255, 205, 86)',
            'rgb(75, 192, 192)',
            'rgb(54, 162, 235)',
            'rgb(153, 102, 255)',
            'rgb(201, 203, 207)'
          ],
          backgroundColor: [
            'rgb(255, 99, 132, 0.35)',
            'rgb(255, 159, 64, 0.35)',
            'rgb(255, 205, 86, 0.35)',
            'rgb(75, 192, 192, 0.35)',
            'rgb(54, 162, 235, 0.35)',
            'rgb(153, 102, 255, 0.35)',
            'rgb(201, 203, 207, 0.35)'
          ],
          borderWidth: 1,
          barPercentage: 1, // more consistent when hovering
        }]
      }

      let options = {
        scales: {
          x: {
            title: {
              display: true,
              text: 'Frequency'
            },
            grid: {
              display: true
            }
          },
          y: {
            grid: {
              display: false
            }
          },
        },
        plugins: {
          legend: {
            display: false
          }
        },
        indexAxis: 'y',
        maintainAspectRatio: false
      }


      return (
        <div>
          <div className="chart-word-frequencies">
            <Bar data={chartData} options={options} />
          </div>
        </div>
      )

    }
  }

}

export default WordFrequencies;