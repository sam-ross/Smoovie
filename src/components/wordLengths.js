import React from "react";
import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

class WordLengths extends React.Component {
  render() {
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

    if (isLoaded === 'waiting') {
      console.log("waiting (wordLengths)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (wordLengths)");
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
          // clip: { left: 0, top: -200, right: 0, bottom: 0 }
          // maintainAspectRatio: false
        }]
      }

      let options = {
        responsive: true,
        plugins: {
          legend: {
            position: 'right',
          }
        },
        layout: {
          padding: {
            left: 30,
          }
        }
      }

      // return chart
      return (
        <section className='section-words-lengths'>
          <h2 className="h2-word-lengths">Word Lengths</h2>
          <div className="chart-outer">
            <div className="chart-circle">
              <Doughnut data={chartData} options={options} />
            </div>
          </div>
        </section>
      )

    }
  }

}

export default WordLengths;