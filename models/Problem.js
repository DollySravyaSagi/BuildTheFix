const mongoose = require('mongoose');

const DuplicateCheckSchema = new mongoose.Schema({
  is_duplicate: { type: Boolean, default: false },
  similar_problem_id: { type: String, default: null },
  similarity_reason: { type: String, default: '' }
}, { _id: false });

const BlueprintSchema = new mongoose.Schema({
  formal_title: { type: String, required: true },
  target_persona: { type: String, required: true },
  tech_stack: [{ type: String }],
  core_features: [{ type: String }],
  roadmap: [{ type: String }],
  duplicate_check: { type: DuplicateCheckSchema, default: () => ({}) }
}, { _id: false });

const ProblemSchema = new mongoose.Schema({
  client_name: { type: String, required: true, trim: true },
  client_email: { type: String, required: true, trim: true },
  raw_title: { type: String, required: true, trim: true },
  raw_description: { type: String, required: true, trim: true },
  status: {
    type: String,
    enum: ['Open', 'In Progress', 'Solved'],
    default: 'Open'
  },
  developer_name: { type: String, default: null },
  developer_github: { type: String, default: null },
  solution_url: { type: String, default: null },
  is_unlocked: { type: Boolean, default: false },
  unlocked_by: [{ type: String }],
  blueprint: { type: BlueprintSchema, required: true }
}, {
  timestamps: true,
  toJSON: {
    virtuals: true,
    transform: (doc, ret) => {
      ret.id = ret._id.toString();
      delete ret.__v;
      return ret;
    }
  }
});

module.exports = mongoose.model('Problem', ProblemSchema);
