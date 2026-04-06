import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CategoryScale, Chart, ChartConfiguration, LinearScale, LineController, LineElement, PointElement, Title, Tooltip } from 'chart.js';
import annotationPlugin, { AnnotationOptions } from 'chartjs-plugin-annotation';
import { PriceData } from '../../model/price-data';
import { CurrInvRow } from '../../model/curr-inv-row';
import { InvestmentLine } from '../../model/investment-line';

@Component({
  selector: 'app-history-chart',
  imports: [],
  templateUrl: './history-chart.html',
  styleUrl: './history-chart.css',
})
export class HistoryChart implements OnInit {
  private graphColor: string = '';
  private chart!: Chart;

  @ViewChild('chart') chartEle!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    Chart.register(LineController,
      LineElement,
      PointElement,
      LinearScale,
      CategoryScale,
      annotationPlugin,
      Title,
      Tooltip)
  }

  chartBuild(pricesData: (PriceData)[], posOrNeg: boolean, currInvestments: CurrInvRow[]): Chart {
    this.chartEle.nativeElement;
    if (posOrNeg) {
      this.graphColor = '#3acc00';
    } else {
      this.graphColor = '#da0700';
    }

    const annotations: Record<number, AnnotationOptions> = {};

    if (currInvestments.length > 0) {
      const investmentLines: InvestmentLine[] = currInvestments.map(currInv => (
        {
          value: currInv.buyPrice,
          label: currInv.buyPrice.toFixed(2),
          color: '#00b3ff'
        }
      ));

      investmentLines.forEach((line, index) => {
        annotations[index] = {
          type: 'line',
          yMin: line.value,
          yMax: line.value,
          borderColor: line.color,
          borderWidth: 2,
          borderDash: [6, 3],
          drawTime: 'afterDatasetsDraw',
          label: {
            display: true,
            content: line.label
          }
        };
      });
    }

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: pricesData.map(priceData => 'Date: ' + priceData.x),
        datasets: [{
          data: pricesData.map(priceData => priceData.y),
          borderColor: this.graphColor,
          borderWidth: 1,
          backgroundColor: this.graphColor,
          stepped: 'middle',
          pointRadius: 0,
          fill: false
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: false
        },
        plugins: {
          annotation: {
            annotations: annotations
          },
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                return ['Close price:' + pricesData[ctx.dataIndex].y.toFixed(2),
                'Open price:' + pricesData[ctx.dataIndex].open.toFixed(2),
                'Low price:' + pricesData[ctx.dataIndex].low.toFixed(2),
                'High price:' + pricesData[ctx.dataIndex].high.toFixed(2)]
              }
            }
          }
        },
        scales: {
          x: {
            grid: {
              display: false
            },
            ticks: {
              display: false
            }
          },
          y: {
            grid: {
              color: 'white'
            },
            ticks: {
              color: 'white'
            },
            border: {
              display: false
            },
            beginAtZero: true
          }
        },
        animation: false
      }
    }
    this.chart = new Chart(this.chartEle.nativeElement, config)
    return this.chart;
  }

  udpateChart(currInvestments: CurrInvRow[]): void {
    const annotations: Record<number, AnnotationOptions> = {};
    const investmentLines: InvestmentLine[] = currInvestments.map(currInv => (
      {
        value: currInv.buyPrice,
        label: currInv.buyPrice.toFixed(2),
        color: '#00b3ff'
      }
    ));

    investmentLines.forEach((line, index) => {
      annotations[index] = {
        type: 'line',
        yMin: line.value,
        yMax: line.value,
        borderColor: line.color,
        borderWidth: 2,
        borderDash: [6, 3],
        drawTime: 'afterDatasetsDraw',
        label: {
          display: true,
          content: line.label
        }
      };
    });
    if (!this.chart.options.plugins) {
      this.chart.options.plugins = {};
    }
    if (!this.chart.options.plugins.annotation) {
      this.chart.options.plugins.annotation = { annotations: {} };
    }
    this.chart.options.plugins.annotation.annotations = annotations;
    this.chart.update();
  }
}
