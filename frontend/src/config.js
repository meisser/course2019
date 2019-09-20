export default {
  // apiURL: 'http://dedi2328.your-server.de:8080',
  apiURL: `http://${window.location.hostname}:8080`,
  stepSizeOptions: [1, 2, 5, 10, 100],
  miniCharts: {
    noOfChartsInSidebar: 5,
    height: 300,
    internalHeight: 150,
  },
  xhrConfig: {
    mode: 'cors',
  },
  handleFetchErrors(response) {
    if (!response.ok) {
      throw Error(response.statusText);
    }
    return response;
  },
  alertError(error) {
    // eslint-disable-next-line no-alert
    alert(error);
  },
};
