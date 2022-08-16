import React, { Component } from 'react';
import ReactDOM from 'react-dom/client';

class MovieForm extends React.Component {
  render() {
    return (
      <form onSubmit={this.props.handeSubmit}>
        <label>
          Movie Name:
          <input type="text" value={this.props.value} onChange={this.props.handleChange} placeholder={"Enter movie title"} />
        </label>
        <input type="submit" value="Submit" />
      </form>
    )
  }
}

export default MovieForm;