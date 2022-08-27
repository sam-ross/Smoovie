import React from "react";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS } from 'chart.js/auto'
import Slider from 'rc-slider';
import 'rc-slider/assets/index.css';

class PhraseFrequencies extends React.Component {
  componentDidMount() {
    window.addEventListener('resize', this.props.changeSliderValue);
    this.props.changeSliderValue(); // ensures that the initial values are correct based on the inital width
  }

  componentDidUpdate() {
    this.props.changeSliderValue();
  }

  render() {
    const isLoaded = this.props.isLoaded;
    const phraseFrequencyRanges = this.props.phraseFrequencyRanges;

    if (isLoaded !== 'waiting' && isLoaded !== 'loading') {
      const sliderCurrentValue = this.props.sliderCurrentValue;
      let entries = Object.entries(phraseFrequencyRanges[sliderCurrentValue - 2].phraseFrequencyRange).slice(0, 50);
      let labels = entries.map((entry) => entry[0]);
      let values = entries.map((entry) => entry[1]);

      let chartData = {
        labels: labels,
        datasets: [{
          label: "Phrase Frequency",
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
          barPercentage: 1,
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
            },
            ticks: {
              font: {
                size: function (context) {
                  // Responsive text resizing for this chart
                  if (context.chart.width > 1290) {
                    return 12;
                  } else if (context.chart.width > 1160) {
                    return 11;
                  } else if (context.chart.width > 860) {
                    return 11;
                  } else if (context.chart.width > 640) {
                    return 11;
                  } else if (context.chart.width > 450) {
                    return 11;
                  } else if (context.chart.width > 375) {
                    return 9.5;
                  } else if (context.chart.width > 305) {
                    return 8.5;
                  } else if (context.chart.width > 100) {
                    return 8;
                  }
                  return;
                }
              }
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
        <section className='section-phrase-frequency'>
          <h2 id="phrase-frequencies-h2">Phrase Frequencies</h2>
          <div className='slider-outer-div'>
            <span className="slider-title">Length of phrase</span>
            <div className="slider-div">
              <Slider
                marks={this.props.sliderMarks}
                min={0}
                max={this.props.sliderMax}
                defaultValue={1}
                onChange={this.props.onChange}
                value={this.props.sliderCurrentValue - 2}
              />
            </div>
          </div>
          <div className="chart-frequencies" id="phrase-frequencies">
            <Bar data={chartData} options={options} />
          </div>
        </section>
      )

    }
  }

}

export default PhraseFrequencies;