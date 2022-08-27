import React from "react";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'
import Toggle from 'react-toggle'
import { defaults } from "chart.js";

class WordFrequencies extends React.Component {

  componentDidUpdate(prevProps) {
    if (prevProps.wordsRetrieved !== this.props.wordsRetrieved) {
      this.props.getWordData();
    }
  }

  render() {
    const isLoaded = this.props.isLoaded;
    const commonRemoved = this.props.commonRemoved;
    let wordsChosen;

    if (isLoaded !== 'waiting' && isLoaded !== 'loading') {
      if (commonRemoved) {
        wordsChosen = this.props.words;
      } else {
        wordsChosen = this.props.wordsAll;
      }

      let entries = Object.entries(wordsChosen).sort((a, b) => b[1] - a[1]).slice(0, 50);
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
              text: 'Frequency',
              font: function (context) {
                // Responsive text resizing for all the charts
                // Responsive legend label padding resizing for donut and pie charts
                if (context.chart.width > 1290) {
                  defaults.font.size = 12;
                  defaults.plugins.legend.labels.padding = 12;
                } else if (context.chart.width > 1160) {
                  defaults.font.size = 11;
                  defaults.plugins.legend.labels.padding = 12;
                } else if (context.chart.width > 860) {
                  defaults.font.size = 11;
                  defaults.plugins.legend.labels.padding = 10;
                } else if (context.chart.width > 640) {
                  defaults.font.size = 11;
                  defaults.plugins.legend.labels.padding = 8;
                } else if (context.chart.width > 460) {
                  defaults.font.size = 11;
                  defaults.plugins.legend.labels.padding = 4;
                } else if (context.chart.width > 100) {
                  defaults.font.size = 10;
                  defaults.plugins.legend.labels.padding = 2;
                }
                return;
              }
            },
            grid: {
              display: true
            }
          },
          y: {
            grid: {
              display: false
            },
          }
        },
        plugins: {
          legend: {
            display: false
          },
        },
        indexAxis: 'y',
        maintainAspectRatio: false,
      }

      return (
        <section className='section-words-frequency' id="word-frequency" >
          <div className='words-frequency-header'>
            <h2>Word Frequencies</h2>
            <div className='word-count-outer'>
              Total word count:
              <div className="word-count">
                {this.props.wordCount}
              </div>
            </div>
          </div>

          <div className='word-frequency-common-words'>
            <label className="word-frequency-label-items">
              <span className='common-words-included'>Common Words Removed</span>
              <Toggle
                defaultChecked={this.props.defaultChecked}
                icons={this.props.icons}
                onChange={this.props.onChange} />
            </label>
          </div>

          <div className="chart-frequencies">
            <Bar data={chartData} options={options} />
          </div>
        </section >
      )

    }
  }

}

export default WordFrequencies;