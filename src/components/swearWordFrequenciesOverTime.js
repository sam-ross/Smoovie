import React from "react";
import { Line } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'

class SwearWordFrequenciesOverTime extends React.Component {
  componentDidUpdate(prevProps) {
    if ((prevProps.isLoaded === "loading" || prevProps.isLoaded === "waiting") && this.props.isLoaded === "done") {
      console.log("scrolling");
      if (this.props.demoMode) {
        setTimeout(function () {
          document.getElementById("word-frequency").scrollIntoView({ behavior: 'smooth' });
        }, 300);
      } else {
        document.getElementById("word-frequency").scrollIntoView({ behavior: 'smooth' });
      }
    }
  }

  render() {
    const isLoaded = this.props.isLoaded;
    const swearWordFrequenciesOverTime = this.props.swearWordFrequenciesOverTime;

    if (isLoaded === 'waiting') {
      console.log("waiting (swearWordFrequenciesOverTime)");
    } else if (isLoaded === 'loading') {
      console.log("loading!!!!! (swearWordFrequenciesOverTime)");
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
              text: 'Movie Sections'
            },
            grid: {
              display: false
            }
          },
          y: {
            title: {
              display: true,
              text: 'Frequency'
            },
            grid: {
              display: true
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
          <section className='section-words-swear-time'>
            <h2>Swear Words Over Time</h2>
            <Line data={chartData} options={options} />
          </section>
        </div>
      )

    }
  }

}

export default SwearWordFrequenciesOverTime;