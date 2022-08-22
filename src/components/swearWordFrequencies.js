import React from "react";
import { Pie } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

class SwearWordFrequencies extends React.Component {
  render() {
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

    if (isLoaded === 'waiting') {
      console.log("waiting (swearWordFrequencies)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (swearWordFrequencies)");
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
            position: 'right',
            // labels: {
            //   font: function (context) {
            //     if (context.chart.width > 590) {          // > 1344px 
            //       defaults.font.size = 20;
            //     } else if (context.chart.width > 530) {   // > 1232px
            //       defaults.font.size = 12;
            //     } else if (context.chart.width > 410) {   // > 
            //       defaults.font.size = 5;
            //     }
            //     return;
            //   }
            // }
          },
        },
        layout: {
          padding: {
            left: 30,
          }
        }
      }

      function swearWordsFunc() {
        if (Object.keys(swearWordFrequencies).length !== 0) {
          return <Pie data={chartData} options={options} />;
        }
        return <h2 className="swear-word-no">No swear words in this movie!</h2 >
      }


      return (
        <section className='section-words-swear'>
          <h2 className="h2-swear-frequencies">Swear Word Frequencies</h2>
          <div className="chart-outer">
            <div className="chart-circle">
              {swearWordsFunc()}
            </div>
          </div>
        </section>
      )

    }
  }

}

export default SwearWordFrequencies;