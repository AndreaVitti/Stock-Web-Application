import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CategoryScale, Chart, ChartConfiguration, LinearScale, LineController, LineElement, PointElement, Title, Tooltip } from 'chart.js';
import { PriceData } from '../../model/price-data';

@Component({
  selector: 'app-history-chart',
  imports: [],
  templateUrl: './history-chart.html',
  styleUrl: './history-chart.css',
})
export class HistoryChart implements OnInit {
  graphColor: string = '';
  
  @ViewChild('chart') chartEle!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    Chart.register(LineController,
      LineElement,
      PointElement,
      LinearScale,
      CategoryScale,
      Title,
      Tooltip)
  }

  chartBuild(pricesData: (PriceData)[], posOrNeg: boolean): Chart {
    this.chartEle.nativeElement;
    if (posOrNeg) {
      this.graphColor = '#3acc00';
    } else {
      this.graphColor = '#da0700';
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
    return new Chart(this.chartEle.nativeElement, config);
  }
}
